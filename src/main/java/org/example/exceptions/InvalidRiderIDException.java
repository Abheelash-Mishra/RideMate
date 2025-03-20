package org.example.exceptions;

public class InvalidRiderIDException extends RuntimeException {
    public InvalidRiderIDException(String message, Throwable cause) {
        super(message, cause);
    }
}
