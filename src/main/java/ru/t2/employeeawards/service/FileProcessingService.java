package ru.t2.employeeawards.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.t2.employeeawards.dto.AwardFileRecord;
import ru.t2.employeeawards.dto.UploadResult;
import ru.t2.employeeawards.exception.FileParseException;
import ru.t2.employeeawards.factory.FileFactory;
import ru.t2.employeeawards.model.Award;
import ru.t2.employeeawards.model.Employee;
import ru.t2.employeeawards.parser.FileParser;
import ru.t2.employeeawards.repository.AwardRepository;
import ru.t2.employeeawards.repository.EmployeeRepository;
import ru.t2.employeeawards.validator.FileValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {
    
    private final FileValidator fileValidator;
    private final FileFactory fileFactory;
    private final EmployeeRepository employeeRepository;
    private final AwardRepository awardRepository;

    /**
     * Обрабатывает загруженный файл: выполняет валидацию, парсинг и сохранение записей о наградах.
     * 
     * @param file загруженный файл с информацией о наградах сотрудников
     * @return результат обработки файла, содержащий статистику по обработанным записям
     * @throws FileParseException если произошла ошибка при валидации или парсинге файла
     * @throws IOException если произошла ошибка при чтении файла
     */
    public UploadResult processFile(MultipartFile file) {
        log.info("Начало обработки файла: {}", file.getOriginalFilename());
        
        try {
            List<AwardFileRecord> records = parseFile(file);
            return processRecords(records);
        } catch (FileParseException e) {
            log.error("Ошибка парсинга файла: {}", e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            throw new FileParseException("Ошибка при чтении файла: " + e.getMessage(), e);
        }
    }

    private List<AwardFileRecord> parseFile(MultipartFile file) throws IOException {
        fileValidator.validate(file);
        FileParser fileParser = fileFactory.getParser(file.getOriginalFilename());
        List<AwardFileRecord> records = fileParser.parse(file.getInputStream());
        log.info("Найдено записей для обработки: {}", records.size());
        return records;
    }

    private UploadResult processRecords(List<AwardFileRecord> records) {
        int processedRecords = 0;
        int createdRecords = 0;
        int updatedRecords = 0;
        int skippedRecords = 0;
        List<String> errors = new ArrayList<>();
        
        for (AwardFileRecord record : records) {
            try {
                boolean created = processRecord(record);
                processedRecords++;
                if (created) {
                    createdRecords++;
                } else {
                    updatedRecords++;
                }
            } catch (RuntimeException e) {
                handleRecordError(record, e, errors);
                skippedRecords++;
            }
        }
        
        log.info("Обработка завершена. Обработано: {}, пропущено: {}, создано: {}, обновлено: {}",
                processedRecords, skippedRecords, createdRecords, updatedRecords);
        
        return new UploadResult(
                records.size(),
                processedRecords,
                skippedRecords,
                updatedRecords,
                createdRecords,
                errors
        );
    }

    private void handleRecordError(AwardFileRecord record, RuntimeException e, List<String> errors) {
        String errorMsg = formatError(record, e);
        if (e instanceof IllegalArgumentException) {
            log.error(errorMsg);
        } else {
            log.error(errorMsg, e);
        }
        errors.add(errorMsg);
    }

    private String formatError(AwardFileRecord record, RuntimeException e) {
        return String.format("Ошибка при обработке записи (employeeId=%d, awardId=%d): %s",
                record.getEmployeeExternalId(), record.getAwardExternalId(), e.getMessage());
    }

    @Transactional
    protected boolean processRecord(AwardFileRecord record) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmployeeExternalId(record.getEmployeeExternalId());
        
        if (employeeOpt.isEmpty()) {
            log.warn("Сотрудник с внешним идентификатором {} не найден", record.getEmployeeExternalId());
            throw new IllegalArgumentException("Сотрудник не найден: " + record.getEmployeeExternalId());
        }
        
        Employee employee = employeeOpt.get();
        Optional<Award> awardOpt = awardRepository.findByAwardExternalId(record.getAwardExternalId());
        
        Award award = awardOpt.orElse(new Award());
        boolean created = awardOpt.isEmpty();
        
        mapRecordToAward(award, employee, record);
        awardRepository.save(award);
        
        return created;
    }
    
    private void mapRecordToAward(Award award, Employee employee, AwardFileRecord record) {
        award.setAwardExternalId(record.getAwardExternalId());
        award.setAwardName(record.getAwardName());
        award.setReceivedDate(record.getReceivedDate());
        award.setEmployee(employee);
    }
}
