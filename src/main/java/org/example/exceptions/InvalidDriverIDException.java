package org.example.exceptions;

public class InvalidDriverIDException extends RuntimeException {
    public InvalidDriverIDException(String message, Throwable cause) {
      super(message, cause);
    }
}
