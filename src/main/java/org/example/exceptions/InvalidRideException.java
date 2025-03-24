package org.example.exceptions;

public class InvalidRideException extends RuntimeException {
    public InvalidRideException(String message) {
        super(message);
    }

    public InvalidRideException(String message, Throwable cause) {
        super(message, cause);
    }
}
