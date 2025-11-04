package ru.t2.employeeawards.DTO;

import java.util.List;
import java.util.ArrayList;

public record UploadResult(
    int totalRecords,
    int processedRecords,
    int skippedRecords,
    int updatedRecords,
    int createdRecords,
    List<String> errors
) {
    public UploadResult {
        if (errors == null) {
            errors = new ArrayList<>();
        }
    }
}
