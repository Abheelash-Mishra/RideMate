package org.example.exceptions;

public class InvalidRiderIDException extends RuntimeException {
    public InvalidRiderIDException() {
        super("INVALID_RIDER_ID");
    }
}
