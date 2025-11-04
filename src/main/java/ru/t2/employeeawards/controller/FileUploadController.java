package ru.t2.employeeawards.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.t2.employeeawards.dto.UploadResult;
import ru.t2.employeeawards.service.FileProcessingService;

@Slf4j
@RestController
@RequestMapping("/api/awards")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileProcessingService fileProcessingService;

    /**
     * Обрабатывает загруженный файл (CSV или Excel) с наградами сотрудников.
     *
     * @param file файл для обработки (CSV, XLS, XLSX)
     * @return результат обработки файла с информацией о количестве обработанных записей
     */
    @PostMapping("/upload")
    public ResponseEntity<UploadResult> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Получен запрос на загрузку файла: {}", file.getOriginalFilename());
        
        if (file.isEmpty()) {
            log.warn("Попытка загрузить пустой файл");
            throw new IllegalArgumentException("Файл не может быть пустым");
        }
        
        UploadResult result = fileProcessingService.processFile(file);
        
        log.info("Файл успешно обработан. Всего записей: {}, обработано: {}, пропущено: {}",
                result.totalRecords(), result.processedRecords(), result.skippedRecords());
        
        if (!result.errors().isEmpty()) {
            log.warn("Обнаружено {} ошибок при обработке файла", result.errors().size());
            result.errors().forEach(error -> log.debug("Ошибка обработки: {}", error));
        }
        
        return ResponseEntity.ok(result);
    }
}
