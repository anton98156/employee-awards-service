package ru.t2.employeeawards.parser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.t2.employeeawards.dto.AwardFileColumn;
import ru.t2.employeeawards.dto.AwardFileRecord;
import ru.t2.employeeawards.exception.FileParseException;
import ru.t2.employeeawards.parser.impl.ExcelParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.t2.employeeawards.utils.AwardFileRecordAssertions.assertRecord;
import static ru.t2.employeeawards.utils.ParserTestData.*;

class ExcelParserTest {

    private ExcelParser excelParser;

    @BeforeEach
    void setUp() {
        excelParser = new ExcelParser();
    }

    @Test
    void parse_WithValidExcel_ShouldReturnRecords() throws IOException {
        InputStream inputStream = createExcelFile(
            EXCEL_HEADERS,
            new Object[]{1247L, "Мария Козлова", 891L, "За выдающиеся результаты в проекте Q4", "2024-03-22"},
            new Object[]{2859L, "Александр Семенов", 1523L, "Благодарность за инициативу и профессионализм", "2024-08-07"}
        );

        List<AwardFileRecord> records = excelParser.parse(inputStream);

        assertThat(records).hasSize(EXPECTED_RECORDS_COUNT);
        
        assertRecord(records.get(FIRST_RECORD_INDEX), FIRST_RECORD);
        assertRecord(records.get(SECOND_RECORD_INDEX), SECOND_RECORD);
    }

    @Test
    void parse_WithEmptyFile_ShouldReturnEmptyList() throws IOException {
        InputStream inputStream = createExcelFile(EXCEL_HEADERS);

        List<AwardFileRecord> records = excelParser.parse(inputStream);

        assertThat(records).isEmpty();
    }

    @Test
    void parse_WithMissingCell_ShouldThrowException() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        Row headerRow = sheet.createRow(FileStructure.HEADERS_ROW_EXCEL.getValue());
        for (int i = 0; i < EXCEL_HEADERS.length; i++) {
            headerRow.createCell(i).setCellValue(EXCEL_HEADERS[i]);
        }
        
        int dataRowIndex = FileStructure.HEADERS_ROW_EXCEL.getValue() + 1;
        Row dataRow = sheet.createRow(dataRowIndex);
        dataRow.createCell(AwardFileColumn.EMPLOYEE_EXTERNAL_ID.getIndex()).setCellValue(1247);
        // Пропускаем ячейку EMPLOYEE_FULL_NAME (ФИО)
        dataRow.createCell(AwardFileColumn.AWARD_EXTERNAL_ID.getIndex()).setCellValue(891);
        dataRow.createCell(AwardFileColumn.AWARD_NAME.getIndex()).setCellValue("За выдающиеся результаты");
        dataRow.createCell(AwardFileColumn.RECEIVED_DATE.getIndex()).setCellValue("2024-03-22");

        InputStream inputStream = convertWorkbookToInputStream(workbook);
        
        assertThatThrownBy(() -> excelParser.parse(inputStream))
                .isInstanceOf(FileParseException.class)
                .hasMessageContaining("Ячейка");
    }

    @Test
    void parse_WithInvalidDate_ShouldThrowException() throws IOException {
        InputStream inputStream = createExcelFile(
            EXCEL_HEADERS,
            new Object[]{1247L, "Мария Козлова", 891L, "За выдающиеся результаты", "invalid-date"}
        );

        assertParseThrowsException(inputStream, "Ошибка при парсинге данных");
    }

    @Test
    void parse_WithInvalidEmployeeId_ShouldThrowException() throws IOException {
        InputStream inputStream = createExcelFile(
            EXCEL_HEADERS,
            new Object[]{"invalid", "Мария Козлова", 891L, "За выдающиеся результаты", "2024-03-22"}
        );

        assertParseThrowsException(inputStream, "Ошибка при парсинге данных");
    }

    private InputStream convertWorkbookToInputStream(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return new java.io.ByteArrayInputStream(outputStream.toByteArray());
    }

    private InputStream createExcelFile(String[] headers, Object[]... dataRows) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        Row headerRow = sheet.createRow(FileStructure.HEADERS_ROW_EXCEL.getValue());
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        for (int rowIndex = 0; rowIndex < dataRows.length; rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            Object[] rowData = dataRows[rowIndex];
            for (int colIndex = 0; colIndex < rowData.length; colIndex++) {
                Cell cell = row.createCell(colIndex);
                Object value = rowData[colIndex];
                if (value instanceof Long) {
                    cell.setCellValue((Long) value);
                } else if (value instanceof String) {
                    cell.setCellValue((String) value);
                }
            }
        }
        
        return convertWorkbookToInputStream(workbook);
    }

    private void assertParseThrowsException(InputStream inputStream, String expectedMessage) {
        assertThatThrownBy(() -> excelParser.parse(inputStream))
                .isInstanceOf(FileParseException.class)
                .hasMessageContaining(expectedMessage);
    }
}
