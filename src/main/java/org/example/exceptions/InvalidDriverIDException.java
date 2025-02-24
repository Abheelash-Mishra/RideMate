package org.example.exceptions;

public class InvalidDriverIDException extends RuntimeException {
    public InvalidDriverIDException() {
      super("INVALID_DRIVER_ID");
    }
}
