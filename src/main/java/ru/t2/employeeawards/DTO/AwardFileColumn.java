package ru.t2.employeeawards.DTO;

public enum AwardFileColumn {
    EMPLOYEE_EXTERNAL_ID(0),
    EMPLOYEE_FULL_NAME(1),
    AWARD_EXTERNAL_ID(2),
    AWARD_NAME(3),
    RECEIVED_DATE(4);
    
    private final int index;
    
    AwardFileColumn(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
}