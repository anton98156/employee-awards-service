package ru.t2.employeeawards.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.t2.employeeawards.dto.AwardFileRecord;
import ru.t2.employeeawards.exception.FileParseException;
import ru.t2.employeeawards.parser.impl.CsvParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.t2.employeeawards.utils.AwardFileRecordAssertions.assertRecord;
import static ru.t2.employeeawards.utils.ParserTestData.*;

class CsvParserTest {

    private CsvParser csvParser;

    @BeforeEach
    void setUp() {
        csvParser = new CsvParser();
    }

    @Test
    void parse_WithValidCsv_ShouldReturnRecords() {
        String csvContent = CSV_HEADER + "\n" +
                "1247,Мария Козлова,891,За выдающиеся результаты в проекте Q4,2024-03-22\n" +
                "2859,Александр Семенов,1523,Благодарность за инициативу и профессионализм,2024-08-07\n";

        List<AwardFileRecord> records = csvParser.parse(createInputStream(csvContent));

        assertThat(records).hasSize(EXPECTED_RECORDS_COUNT);
        assertRecord(records.get(FIRST_RECORD_INDEX), FIRST_RECORD);
        assertRecord(records.get(SECOND_RECORD_INDEX), SECOND_RECORD);
    }

    @Test
    void parse_WithEmptyFile_ShouldReturnEmptyList() {
        String csvContent = CSV_HEADER + "\n";
        List<AwardFileRecord> records = csvParser.parse(createInputStream(csvContent));

        assertThat(records).isEmpty();
    }

    @Test
    void parse_WithInvalidColumnCount_ShouldThrowException() {
        String csvContent = CSV_HEADER + "\n" +
                "1247,Мария Козлова,891\n";

        assertParseThrowsException(csvContent, "неверное количество колонок");
    }

    @Test
    void parse_WithInvalidDate_ShouldThrowException() {
        String csvContent = CSV_HEADER + "\n" +
                "1247,Мария Козлова,891,За выдающиеся результаты,invalid-date\n";

        assertParseThrowsException(csvContent, "Ошибка при парсинге данных");
    }

    @Test
    void parse_WithInvalidEmployeeId_ShouldThrowException() {
        String csvContent = CSV_HEADER + "\n" +
                "invalid,Мария Козлова,891,За выдающиеся результаты,2024-03-22\n";

        assertParseThrowsException(csvContent, "Ошибка при парсинге данных");
    }

    private InputStream createInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    private void assertParseThrowsException(String csvContent, String expectedMessage) {
        assertThatThrownBy(() -> csvParser.parse(createInputStream(csvContent)))
                .isInstanceOf(FileParseException.class)
                .hasMessageContaining(expectedMessage);
    }
}
