package ru.t2.employeeawards.Parser.Impl;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import ru.t2.employeeawards.DTO.AwardFileRecord;
import ru.t2.employeeawards.Parser.FileParser;
import ru.t2.employeeawards.Parser.FileStructure;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import java.util.ArrayList;
import ru.t2.employeeawards.DTO.AwardFileColumn;
import ru.t2.employeeawards.Exception.FileParseException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Cell;
import ru.t2.employeeawards.Parser.utils.RecordParserUtils;

public class ExcelParser implements FileParser {
    private static final int FIRST_SHEET_INDEX = 0;

    /**
     * Парсит Excel файл и возвращает список записей о наградах.
     *
     * @param inputStream поток данных Excel файла
     * @return список записей о наградах
     */
    @Override
    public List<AwardFileRecord> parse(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(FIRST_SHEET_INDEX);
            if (sheet == null) {
                throw new FileParseException("Не удалось получить лист из файла");
            }
            
            List<AwardFileRecord> records = new ArrayList<>();
            DataFormatter dataFormatter = new DataFormatter();
            for (Row row : sheet) {
                if (row.getRowNum() == FileStructure.HEADERS_ROW_EXCEL.getValue()) {
                    continue; // Пропускаем строку заголовков
                }
                validateRow(row);
                records.add(parseRow(row, dataFormatter));
            }

            return records;
        } catch (IOException e) {
            throw new FileParseException("Ошибка при чтении Excel файла: " + e.getMessage(), e);
        }
    }

    private void validateRow(Row row) {
        if (row == null) {
            throw new FileParseException("Строка пустая");
        }
        
        for (int i = 0; i < FileStructure.EXPECTED_COLUMNS.getValue(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                throw new FileParseException(
                    "Ячейка " + i + " пустая в строке " + (row.getRowNum() + 1)
                );
            }
        }
    }

    private AwardFileRecord parseRow(Row row, DataFormatter dataFormatter) {
        Cell[] cells = extractCells(row);
        String[] values = formatCells(cells, dataFormatter);
        return createRecord(values, row);
    }

    private Cell[] extractCells(Row row) {
        return new Cell[]{
            row.getCell(AwardFileColumn.EMPLOYEE_EXTERNAL_ID.getIndex()),
            row.getCell(AwardFileColumn.EMPLOYEE_FULL_NAME.getIndex()),
            row.getCell(AwardFileColumn.AWARD_EXTERNAL_ID.getIndex()),
            row.getCell(AwardFileColumn.AWARD_NAME.getIndex()),
            row.getCell(AwardFileColumn.RECEIVED_DATE.getIndex())
        };
    }

    private String[] formatCells(Cell[] cells, DataFormatter dataFormatter) {
        String[] values = new String[cells.length];
        for (int i = 0; i < cells.length; i++) {
            values[i] = dataFormatter.formatCellValue(cells[i]);
        }
        return values;
    }

    private AwardFileRecord createRecord(String[] values, Row row) {
        String errorContext = "строке " + (row.getRowNum() + 1);
        return RecordParserUtils.createRecord(values, errorContext);
    }
}
