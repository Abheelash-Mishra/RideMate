package org.example.controllers;

import org.example.repository.Database;
import org.example.services.driver.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public class DriverController {
    @Autowired
    private DriverService driverService;

    @GetMapping("/driver/add")
    @ResponseBody
    public void addDriver(@RequestParam String driverID, @RequestParam int x, @RequestParam int y) {
        driverService.addDriver(driverID, x, y);
    }

    @GetMapping("/driver/rate")
    @ResponseBody
    public String rateDriver(@RequestParam String driverID, @RequestParam float rating) {
        return driverService.rateDriver(driverID, rating);
    }
}
