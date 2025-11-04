package ru.t2.employeeawards.parser;

public enum FileStructure {
    HEADERS_ROW_EXCEL(0),
    HEADERS_ROW_CSV(1),
    EXPECTED_COLUMNS(5);
    
    private final int value;
    
    FileStructure(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}