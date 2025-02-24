package org.example.services.ride.exceptions;

public class NoDriversException extends RuntimeException {
    public NoDriversException() {
        super("NO_DRIVERS_AVAILABLE");
    }
}
