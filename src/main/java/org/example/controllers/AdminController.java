package org.example.controllers;

import org.example.dto.DriverDTO;
import org.example.dto.DriverEarningsDTO;
import org.example.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @DeleteMapping("/drivers/remove")
    public ResponseEntity<Boolean> removeDriver(@RequestParam("driverID") long driverID) {
        log.info("Accessing endpoint: /admin/drivers/remove");

        return ResponseEntity.ok(adminService.removeDriver(driverID));
    }

    @GetMapping("/drivers/list")
    public ResponseEntity<List<DriverDTO>> listNDriverDetails(@RequestParam("N") int N) {
        return ResponseEntity.ok(adminService.listNDriverDetails(N));
    }

    @GetMapping("/drivers/earnings")
    public ResponseEntity<DriverEarningsDTO> getDriverEarnings(@RequestParam("driverID") long driverID) {
        log.info("Accessing endpoint: /admin/drivers/earnings");

        return ResponseEntity.ok(adminService.getDriverEarnings(driverID));
    }
}
