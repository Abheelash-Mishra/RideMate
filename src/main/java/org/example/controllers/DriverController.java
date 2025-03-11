package org.example.controllers;

import org.example.dto.DriverRatingDTO;
import org.example.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @PostMapping("/add")
    public ResponseEntity<String> addDriver(
            @RequestParam("driverID") String driverID,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        driverService.addDriver(driverID, x, y);
        return ResponseEntity.status(HttpStatus.CREATED).body("Driver Added!");
    }

    @PostMapping("/rate")
    public ResponseEntity<DriverRatingDTO> rateDriver(
            @RequestParam("driverID") String driverID,
            @RequestParam("rating") float rating
    ) {
        return ResponseEntity.ok(driverService.rateDriver(driverID, rating));
    }
}
