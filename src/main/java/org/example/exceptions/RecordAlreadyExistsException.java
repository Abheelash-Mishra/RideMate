package org.example.exceptions;

public class RecordAlreadyExistsException extends RuntimeException {
    public RecordAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
