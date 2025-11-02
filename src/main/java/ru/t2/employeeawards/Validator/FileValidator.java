package ru.t2.employeeawards.Validator;

import ru.t2.employeeawards.Exception.FileParseException;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileValidator {
    private static final int ZIP_SIGNATURE_LENGTH = 4;
    private static final byte[] ZIP_SIGNATURE = {0x50, 0x4B, 0x03, 0x04};

    /**
     * Проверяет файл в зависимости от его расширения.
     *
     * @param file файл для валидации
     * @throws FileParseException если файл невалидный
     */
    public void validate(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new FileParseException("Имя файла не указано");
        }

        String normalizedFileName = fileName.toLowerCase();
        if (normalizedFileName.endsWith(".xls")) {
            validateXlsFile(file);
        } else if (normalizedFileName.endsWith(".xlsx")) {
            validateXlsxFile(file);
        } else {
            throw new FileParseException("Неподдерживаемый тип файла: " + fileName);
        }
    }

    private void validateXlsFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // Попытка создать Workbook - если успешно, файл валидный
            Workbook workbook = WorkbookFactory.create(inputStream);
            workbook.close(); // Закрываем сразу после проверки
        } catch (Exception e) {
            throw new FileParseException("Невалидный xls файл: " + e.getMessage(), e);
        }
    }

    private void validateXlsxFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // Проверка "ZIP signature"
            if (!isZipFile(inputStream)) {
                throw new FileParseException("Файл не является валидным ZIP архивом (xlsx должен быть ZIP)");
            }

            // Проверка структуры Excel внутри ZIP
            try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
                boolean hasXlFolder = false;
                ZipEntry entry;

                while ((entry = zipInputStream.getNextEntry()) != null) {
                    if (entry.getName().startsWith("xl/")) {
                        hasXlFolder = true;
                        break;
                    }
                }

                if (!hasXlFolder) {
                    throw new FileParseException("Файл не является валидным Excel файлом: отсутствует структура xl/");
                }
            }
        } catch (IOException e) {
            throw new FileParseException("Ошибка при проверке xlsx файла: " + e.getMessage(), e);
        }
    }

    private boolean isZipFile(InputStream inputStream) throws IOException {
        byte[] signature = new byte[ZIP_SIGNATURE_LENGTH];
        int bytesRead = inputStream.read(signature);

        if (bytesRead != ZIP_SIGNATURE_LENGTH) {
            return false;
        }

        for (int i = 0; i < ZIP_SIGNATURE_LENGTH; i++) {
            if (signature[i] != ZIP_SIGNATURE[i]) {
                return false;
            }
        }

        return true;
    }
}
