package ru.t2.employeeawards.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.t2.employeeawards.dto.UploadResult;
import ru.t2.employeeawards.exception.FileParseException;
import ru.t2.employeeawards.service.FileProcessingService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    private static final String UPLOAD_ENDPOINT = "/api/awards/upload";
    private static final String CSV_CONTENT = "ID сотрудника,ФИО сотрудника,ID награды,Название награды,Дата получения\n" +
            "1247,Мария Козлова,891,Награда,2025-03-22\n";
    
    private static final int EMPTY_ARRAY_SIZE = 0;
    private static final int NO_RECORDS = 0;
    private static final int ONE_RECORD = 1;
    private static final int TWO_RECORDS = 2;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileProcessingService fileProcessingService;

    private MockMultipartFile validFile;

    @BeforeEach
    void setUp() {
        validFile = new MockMultipartFile("file", "test.csv", "text/csv", CSV_CONTENT.getBytes());
    }

    @Test
    void uploadFile_WithValidFile_ShouldReturnSuccess() throws Exception {
        when(fileProcessingService.processFile(any())).thenReturn(new UploadResult(ONE_RECORD, ONE_RECORD, NO_RECORDS, new ArrayList<>()));

        mockMvc.perform(multipart(UPLOAD_ENDPOINT).file(validFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.processedRecords").value(ONE_RECORD));
    }

    @Test
    void uploadFile_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[EMPTY_ARRAY_SIZE]);

        mockMvc.perform(multipart(UPLOAD_ENDPOINT).file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Файл не может быть пустым"));
    }

    @Test
    void uploadFile_WithProcessingErrors_ShouldReturnResultWithErrors() throws Exception {
        String errorMessage = "Ошибка при обработке записи (employeeId=999, awardId=500): Сотрудник не найден: 999";
        List<String> errors = List.of(errorMessage);
        when(fileProcessingService.processFile(any())).thenReturn(new UploadResult(TWO_RECORDS, ONE_RECORD, ONE_RECORD, errors));

        mockMvc.perform(multipart(UPLOAD_ENDPOINT).file(validFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processedRecords").value(ONE_RECORD))
                .andExpect(jsonPath("$.skippedRecords").value(ONE_RECORD))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void uploadFile_WithFileParseException_ShouldReturnBadRequest() throws Exception {
        String errorMessage = "Неподдерживаемый тип файла";
        when(fileProcessingService.processFile(any())).thenThrow(new FileParseException(errorMessage));

        mockMvc.perform(multipart(UPLOAD_ENDPOINT).file(validFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }
}
