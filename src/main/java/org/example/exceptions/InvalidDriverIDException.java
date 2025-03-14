package org.example.exceptions;

public class InvalidDriverIDException extends RuntimeException {
    public InvalidDriverIDException(long driverID, Throwable cause) {
      super("INVALID_DRIVER_ID " + driverID, cause);
    }
}
