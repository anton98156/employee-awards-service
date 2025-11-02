package ru.t2.employeeawards.Exception;

public class FileParseException extends RuntimeException {
    public FileParseException(String message) {
        super(message);
    }
    public FileParseException(Throwable cause) {
        super(cause);
    }
    public FileParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
