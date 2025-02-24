package org.example.exceptions;

public class InvalidRideException extends RuntimeException {
    public InvalidRideException() {
        super("INVALID_RIDE");
    }
}
