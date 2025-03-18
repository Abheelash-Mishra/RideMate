package org.example.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidDriverIDException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDriverID(InvalidDriverIDException ex) {
        log.warn("Database cannot fetch details of driver as it does not exist", ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidRideException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRideID(InvalidRideException ex) {
        log.warn("Database cannot fetch details of the ride as it does not exist", ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NoDriversException.class)
    public ResponseEntity<Map<String, String>> handleNoDrivers(NoDriversException ex) {
        log.warn("No drivers are available for the rider", ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidRiderIDException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRiderID(InvalidRiderIDException ex) {
        log.warn("Database cannot fetch details of rider as it does not exist", ex);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
}

