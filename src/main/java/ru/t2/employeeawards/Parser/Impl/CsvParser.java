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
import java.util.ArrayList;
import ru.t2.employeeawards.Parser.FileStructure;
import java.util.Arrays;
import ru.t2.employeeawards.Parser.utils.RecordParserUtils;

public class CsvParser implements FileParser {

    /**
     * Парсит CSV файл и возвращает список записей о наградах.
     *
     * @param inputStream поток данных CSV файла
     * @return список записей о наградах
     */
    @Override
    public List<AwardFileRecord> parse(InputStream inputStream) {
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
        String errorContext = "строке: " + Arrays.toString(line);
        records.add(RecordParserUtils.createRecord(line, errorContext));
    }
}
