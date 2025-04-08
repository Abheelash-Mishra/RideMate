package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverRatingDTO;
import org.example.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @PostMapping("/add")
    public ResponseEntity<Long> addDriver(
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        log.info("Accessing endpoint: /driver/add");

        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.addDriver(email, phoneNumber, x, y));
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
