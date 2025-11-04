package ru.t2.employeeawards.Factory;

import org.springframework.stereotype.Component;
import ru.t2.employeeawards.Exception.FileParseException;
import ru.t2.employeeawards.Parser.FileParser;
import ru.t2.employeeawards.Parser.Impl.CsvParser;
import ru.t2.employeeawards.Parser.Impl.ExcelParser;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class FileFactory {
    private static final String EXTENSION_XLSX = ".xlsx";
    private static final String EXTENSION_XLS = ".xls";
    private static final String EXTENSION_CSV = ".csv";

    // Финальная мапа с парсерами
    private static final Map<String, Supplier<FileParser>> PARSER_SUPPLIERS;
    static {
        PARSER_SUPPLIERS = Map.of(
                EXTENSION_XLSX, ExcelParser::new,
                EXTENSION_XLS, ExcelParser::new,
                EXTENSION_CSV, CsvParser::new
        );
    }

    /**
     * Возвращает парсер для указанного файла на основе его расширения.
     *
     * @param fileName имя файла
     * @return парсер для данного типа файла
     * @throws FileParseException если тип файла не поддерживается
     */
    public FileParser getParser(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new FileParseException("Имя файла не указано");
        }

        String normalizedFileName = fileName.toLowerCase();
        String extension = extractExtension(normalizedFileName);

        Supplier<FileParser> parserSupplier = PARSER_SUPPLIERS.get(extension);
        if (parserSupplier == null) {
            throw new FileParseException("Неподдерживаемый тип файла: " + fileName);
        }

        return parserSupplier.get();
    }

    /**
     * Извлекает расширение файла из имени.
     *
     * @param fileName имя файла
     * @return расширение файла или исключение если расширение не найдено
     */
    private String extractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex <= 0 || lastDotIndex == fileName.length() - 1) {
            throw new FileParseException("Файл не имеет расширения: " + fileName);
        }
        return fileName.substring(lastDotIndex);
    }

}
