package org.example.exceptions;

public class InvalidRideException extends RuntimeException {
    public InvalidRideException(long rideID, Throwable cause) {
        super("INVALID_RIDE " + rideID, cause);
    }
}
