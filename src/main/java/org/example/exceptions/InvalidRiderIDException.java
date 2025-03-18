package org.example.exceptions;

public class InvalidRiderIDException extends RuntimeException {
    public InvalidRiderIDException(long riderID, Throwable cause) {
        super("INVALID_RIDER_ID " + riderID, cause);
    }
}
