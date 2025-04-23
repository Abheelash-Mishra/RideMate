package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverRatingDTO;
import org.example.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

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

        String emailRegex = "^[a-zA-Z0-9_.±]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if (!Pattern.matches(emailRegex, email)) {
            log.warn("Invalid email ID was used. Driver was not registered");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1L);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.addDriver(email, phoneNumber, x, y));
    }

    @PostMapping("/rate")
    public ResponseEntity<DriverRatingDTO> rateDriver(
            @RequestParam("rideID") long rideID,
            @RequestParam("driverID") long driverID,
            @RequestParam("rating") float rating,
            @RequestParam("comment") String comment
    ) {
        log.info("Accessing endpoint: /driver/rate || PARAMS: driverID={}, rating={}, comment='{}'", driverID, rating, comment);

        return ResponseEntity.ok(driverService.rateDriver(rideID, driverID, rating, comment));
    }
}
