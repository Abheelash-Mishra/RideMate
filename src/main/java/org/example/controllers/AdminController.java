package org.example.controllers;

import org.example.services.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/drivers/remove")
    public String removeDriver(@RequestParam("driverID") String driverID) {
        return adminService.removeDriver(driverID);
    }

    @GetMapping("/drivers/list")
    public String listNDrivers(@RequestParam("N") int N) {
        List<String> driverDetails = adminService.listNDriverDetails(N);

        return String.join(", ", driverDetails);
    }

    @GetMapping("/drivers/earnings")
    public float getDriverEarnings(@RequestParam("driverID") String driverID) {
        return adminService.getDriverEarnings(driverID);
    }
}
