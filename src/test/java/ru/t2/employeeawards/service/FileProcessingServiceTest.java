package ru.t2.employeeawards.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.t2.employeeawards.dto.AwardFileRecord;
import ru.t2.employeeawards.dto.UploadResult;
import ru.t2.employeeawards.exception.FileParseException;
import ru.t2.employeeawards.factory.FileFactory;
import ru.t2.employeeawards.model.Award;
import ru.t2.employeeawards.model.Employee;
import ru.t2.employeeawards.parser.FileParser;
import ru.t2.employeeawards.repository.AwardRepository;
import ru.t2.employeeawards.repository.EmployeeRepository;
import ru.t2.employeeawards.validator.FileValidator;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {
    private static final String TEST_FILE_NAME = "test.csv";
    private static final long VALID_EMPLOYEE_ID = 1247L;
    private static final long INVALID_EMPLOYEE_ID = 9999L;
    private static final long VALID_AWARD_ID = 891L;

    private static final int NO_RECORDS = 0;
    private static final int ONE_RECORD = 1;
    private static final int ONE_ERROR = 1;
    private static final int ONE_SAVE_OPERATION = 1;
    private static final int FIRST_ARGUMENT_INDEX = 0;

    @Mock
    private FileValidator fileValidator;
    @Mock
    private FileFactory fileFactory;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AwardRepository awardRepository;
    @Mock
    private FileParser fileParser;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileProcessingService fileProcessingService;

    private Employee employee;
    private AwardFileRecord validRecord;
    private AwardFileRecord invalidRecord;

    @BeforeEach
    void setUp() throws IOException {
        employee = createEmployee(VALID_EMPLOYEE_ID, "Мария Козлова");
        validRecord = createRecord(new RecordData(
                VALID_EMPLOYEE_ID,
                "Мария Козлова",
                VALID_AWARD_ID,
                "Награда",
                LocalDate.of(2025, 3, 22)
        ));
        invalidRecord = createRecord(new RecordData(
                INVALID_EMPLOYEE_ID,
                "Несуществующий",
                500L,
                "Награда",
                LocalDate.of(2025, 1, 1)
        ));

        lenient().when(multipartFile.getOriginalFilename()).thenReturn(TEST_FILE_NAME);
        lenient().when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));
    }

    @Test
    void processFile_WithValidRecord_ShouldProcessSuccessfully() throws IOException {
        setupParserMock(List.of(validRecord));
        when(employeeRepository.findByEmployeeExternalId(VALID_EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(awardRepository.findByAwardExternalId(VALID_AWARD_ID)).thenReturn(Optional.empty());
        when(awardRepository.save(any(Award.class))).thenAnswer(invocation -> invocation.getArgument(FIRST_ARGUMENT_INDEX));

        UploadResult result = fileProcessingService.processFile(multipartFile);

        assertThat(result.processedRecords()).isEqualTo(ONE_RECORD);
        assertThat(result.skippedRecords()).isEqualTo(NO_RECORDS);
        verify(awardRepository).save(any(Award.class));
    }

    @Test
    void processFile_WithNonExistentEmployee_ShouldSkipRecord() throws IOException {
        setupParserMock(List.of(invalidRecord));
        when(employeeRepository.findByEmployeeExternalId(INVALID_EMPLOYEE_ID)).thenReturn(Optional.empty());

        UploadResult result = fileProcessingService.processFile(multipartFile);

        assertThat(result.processedRecords()).isEqualTo(NO_RECORDS);
        assertThat(result.skippedRecords()).isEqualTo(ONE_RECORD);
        assertThat(result.errors()).hasSize(ONE_ERROR);
        verify(awardRepository, never()).save(any(Award.class));
    }

    @Test
    void processFile_WithMixedRecords_ShouldProcessPartially() throws IOException {
        setupParserMock(List.of(validRecord, invalidRecord));
        when(employeeRepository.findByEmployeeExternalId(VALID_EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmployeeExternalId(INVALID_EMPLOYEE_ID)).thenReturn(Optional.empty());
        when(awardRepository.findByAwardExternalId(VALID_AWARD_ID)).thenReturn(Optional.empty());
        when(awardRepository.save(any(Award.class))).thenAnswer(invocation -> invocation.getArgument(FIRST_ARGUMENT_INDEX));

        UploadResult result = fileProcessingService.processFile(multipartFile);

        assertThat(result.processedRecords()).isEqualTo(ONE_RECORD);
        assertThat(result.skippedRecords()).isEqualTo(ONE_RECORD);
        verify(awardRepository, times(ONE_SAVE_OPERATION)).save(any(Award.class));
    }

    @Test
    void processFile_WithValidationError_ShouldThrowException() throws IOException {
        doThrow(new FileParseException("Файл невалидный")).when(fileValidator).validate(multipartFile);

        assertThatThrownBy(() -> fileProcessingService.processFile(multipartFile))
                .isInstanceOf(FileParseException.class)
                .hasMessage("Файл невалидный");
    }

    private void setupParserMock(List<AwardFileRecord> records) throws IOException {
        when(fileFactory.getParser(anyString())).thenReturn(fileParser);
        when(fileParser.parse(any(InputStream.class))).thenReturn(records);
    }

    private Employee createEmployee(long externalId, String fullName) {
        Employee emp = new Employee();
        emp.setEmployeeExternalId(externalId);
        emp.setFullName(fullName);
        return emp;
    }

    private AwardFileRecord createRecord(RecordData data) {
        return new AwardFileRecord(
                data.employeeId(),
                data.employeeName(),
                data.awardId(),
                data.awardName(),
                data.receivedDate()
        );
    }

    private record RecordData(
        long employeeId,
        String employeeName,
        long awardId,
        String awardName,
        LocalDate receivedDate
    ) {}
}
