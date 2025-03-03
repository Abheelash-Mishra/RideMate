package org.example.controllers;

import org.example.services.driver.DriverService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @PostMapping("/add")
    public void addDriver(
            @RequestParam("driverID") String driverID,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        driverService.addDriver(driverID, x, y);
    }

    @PostMapping("/rate")
    public ResponseEntity<Map<String, Object>> rateDriver(
            @RequestParam("driverID") String driverID,
            @RequestParam("rating") float rating
    ) {
        return ResponseEntity.ok(driverService.rateDriver(driverID, rating));
    }
}
