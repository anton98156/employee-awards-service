package ru.t2.employeeawards.Parser.utils;

import ru.t2.employeeawards.DTO.AwardFileRecord;
import ru.t2.employeeawards.DTO.AwardFileColumn;
import ru.t2.employeeawards.Exception.FileParseException;

import java.time.DateTimeException;
import java.time.LocalDate;

/**
 * Утилитарный класс для парсинга записей о наградах из файлов.
 */
public final class RecordParserUtils {

    /**
     * Создает объект AwardFileRecord из массива строковых значений.
     *
     * @param values массив строковых значений для создания записи
     * @param errorContext контекст ошибки для сообщения (например, номер строки или содержимое строки)
     * @return созданный объект AwardFileRecord
     * @throws FileParseException если произошла ошибка при парсинге данных
     */
    public static AwardFileRecord createRecord(String[] values, String errorContext) {
        try {
            return new AwardFileRecord(
                Long.parseLong(values[AwardFileColumn.EMPLOYEE_EXTERNAL_ID.getIndex()]),
                values[AwardFileColumn.EMPLOYEE_FULL_NAME.getIndex()],
                Long.parseLong(values[AwardFileColumn.AWARD_EXTERNAL_ID.getIndex()]),
                values[AwardFileColumn.AWARD_NAME.getIndex()],
                LocalDate.parse(values[AwardFileColumn.RECEIVED_DATE.getIndex()])
            );
        } catch (NumberFormatException | DateTimeException e) {
            throw new FileParseException(
                "Ошибка при парсинге данных: " + e.getMessage() + " в " + errorContext,
                e
            );
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            throw new FileParseException(
                "Отсутствуют обязательные данные в " + errorContext,
                e
            );
        }
    }
}

