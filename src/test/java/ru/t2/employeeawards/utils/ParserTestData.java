package ru.t2.employeeawards.utils;

import ru.t2.employeeawards.dto.AwardFileRecord;

import java.time.LocalDate;

/*
 * Утилитарный класс с тестовыми данными для парсеров
 */
public final class ParserTestData {
    public static final int FIRST_RECORD_INDEX = 0;
    public static final int SECOND_RECORD_INDEX = 1;
    public static final int EXPECTED_RECORDS_COUNT = 2;
    
    public static final String CSV_HEADER = "ID сотрудника,ФИО сотрудника,ID награды,Название награды,Дата получения";
    public static final String[] EXCEL_HEADERS = {
        "ID сотрудника", "ФИО сотрудника", "ID награды", "Название награды", "Дата получения"
    };

    private ParserTestData() {
        // Утилитарный класс
    }

    public static final AwardFileRecord FIRST_RECORD = new AwardFileRecord(
        1247L, 
        "Мария Козлова", 
        891L, 
        "За выдающиеся результаты в проекте Q4", 
        LocalDate.of(2024, 3, 22)
    );

    public static final AwardFileRecord SECOND_RECORD = new AwardFileRecord(
        2859L, 
        "Александр Семенов", 
        1523L, 
        "Благодарность за инициативу и профессионализм", 
        LocalDate.of(2024, 8, 7)
    );
}

