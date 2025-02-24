package org.example.services.admin.exceptions;

public class InvalidDriverIDException extends RuntimeException {
    public InvalidDriverIDException() {
      super("INVALID_DRIVER_ID");
    }
}
