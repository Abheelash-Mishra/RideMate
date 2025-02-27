package org.example.controllers;

import org.example.services.driver.DriverService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @GetMapping("/add")
    public String addDriver(
            @RequestParam("driverID") String driverID,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        driverService.addDriver(driverID, x, y);
        return "Added";
    }

    @GetMapping("/rate")
    public String rateDriver(
            @RequestParam("driverID") String driverID,
            @RequestParam("rating") float rating
    ) {
        return driverService.rateDriver(driverID, rating);
    }
}
