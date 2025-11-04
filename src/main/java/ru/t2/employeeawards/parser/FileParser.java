package ru.t2.employeeawards.parser;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import ru.t2.employeeawards.dto.AwardFileRecord;

/**
 * Интерфейс для парсинга файлов с наградами сотрудников.
 */
public interface FileParser {
    /**
     * Парсит файл и возвращает список записей о наградах.
     *
     * @param inputStream поток данных файла
     * @return список записей о наградах
     * @throws IOException если произошла ошибка при чтении файла
     */
    List<AwardFileRecord> parse(InputStream inputStream) throws IOException;
}
