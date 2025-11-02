package ru.t2.employeeawards.Exception;
/**
 * Исключение, выбрасываемое при ошибках парсинга файла.
 * 
 * Может использоваться для оборачивания низкоуровневых исключений или для передачи
 * пользовательского сообщения об ошибке.
 */
public class FileParseException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением.
     *
     * @param message описание причины ошибки
     */
    public FileParseException(String message) {
        super(message);
    }

    /**
     * Создает исключение с указанной причиной.
     *
     * @param cause причина ошибки
     */
    public FileParseException(Throwable cause) {
        super(cause);
    }

    /**
     * Создает исключение с указанным сообщением и причиной.
     *
     * @param message описание причины ошибки
     * @param cause причина ошибки
     */
    public FileParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
