package org.example.exceptions;

public class InvalidDriverIDException extends RuntimeException {
    public InvalidDriverIDException(String driverID) {
      super("INVALID_DRIVER_ID " + driverID);
    }
}
