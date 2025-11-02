package ru.t2.employeeawards.Parser.Impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import ru.t2.employeeawards.DTO.AwardFileRecord;
import ru.t2.employeeawards.Parser.FileParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import ru.t2.employeeawards.Exception.FileParseException;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.time.LocalDate;
import ru.t2.employeeawards.Parser.FileStructure;
import ru.t2.employeeawards.DTO.AwardFileColumn;
import java.util.Arrays;

public class CsvParser implements FileParser {

    /**
     * Парсит CSV файл и возвращает список записей о наградах.
     *
     * @param inputStream поток данных CSV файла
     * @return список записей о наградах
     * @throws IOException если произошла ошибка при чтении файла
     */
    @Override
    public List<AwardFileRecord> parse(InputStream inputStream) throws IOException {
        List<AwardFileRecord> records = new ArrayList<>();
        
        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withSkipLines(FileStructure.HEADERS_ROW_CSV.getValue()) // пропускаем заголовки
                .build()) { 
                
            String[] line;
            while ((line = reader.readNext()) != null) {
                validateLine(line);
                parseLine(line, records);
            }
        } catch (CsvValidationException | IOException e) {
            throw new FileParseException("Ошибка при чтении CSV файла: " + e.getMessage(), e);
        } 

        return records;
    }

    private void validateLine(String[] line) {
        if (line.length != FileStructure.EXPECTED_COLUMNS.getValue()) {
            throw new FileParseException("Строка содержит неверное количество колонок: " + line.length);
        }
    }

    private void parseLine(String[] line, List<AwardFileRecord> records) {
        try {
            records.add(new AwardFileRecord(
                Long.parseLong(line[AwardFileColumn.EMPLOYEE_EXTERNAL_ID.getIndex()]),
                line[AwardFileColumn.EMPLOYEE_FULL_NAME.getIndex()],
                Long.parseLong(line[AwardFileColumn.AWARD_EXTERNAL_ID.getIndex()]),
                line[AwardFileColumn.AWARD_NAME.getIndex()],
                LocalDate.parse(line[AwardFileColumn.RECEIVED_DATE.getIndex()])
            ));
        } catch (NumberFormatException | DateTimeException | NullPointerException e) {
            throw new FileParseException("Ошибка при парсинге данных: " + e.getMessage() + " в строке: " + Arrays.toString(line), e);
        } 
    }
}
