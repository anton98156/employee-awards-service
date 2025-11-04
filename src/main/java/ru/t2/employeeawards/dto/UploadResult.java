package ru.t2.employeeawards.dto;

import java.util.List;
import java.util.ArrayList;

public record UploadResult(
    int totalRecords,
    int processedRecords,
    int skippedRecords,
    List<String> errors
) {
    public UploadResult {
        if (errors == null) {
            errors = new ArrayList<>();
        }
    }
}
