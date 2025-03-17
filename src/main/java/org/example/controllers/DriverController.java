package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverRatingDTO;
import org.example.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @PostMapping("/add")
    public ResponseEntity<String> addDriver(
            @RequestParam("driverID") long driverID,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        log.info("Accessing endpoint: /driver/add || PARAMS: driverID={}, x={}, y={}", driverID, x, y);

        try {
            driverService.addDriver(driverID, x, y);

            return ResponseEntity.status(HttpStatus.CREATED).body("Driver Added!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Driver not added!");
        }
    }

    @PostMapping("/rate")
    public ResponseEntity<DriverRatingDTO> rateDriver(
            @RequestParam("driverID") long driverID,
            @RequestParam("rating") float rating
    ) {
        log.info("Accessing endpoint: /driver/rate || PARAMS: driverID={}, rating={}", driverID, rating);

        return ResponseEntity.ok(driverService.rateDriver(driverID, rating));
    }
}
