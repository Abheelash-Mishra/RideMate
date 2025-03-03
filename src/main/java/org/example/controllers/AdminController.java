package org.example.controllers;

import org.example.dto.DriverDTO;
import org.example.dto.DriverEarningsDTO;
import org.example.services.admin.AdminService;
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
    public boolean removeDriver(@RequestParam("driverID") String driverID) {
        return adminService.removeDriver(driverID);
    }

    @GetMapping(value = "/drivers/list", produces = "application/json")
    public ResponseEntity<List<DriverDTO>> listNDriverDetails(@RequestParam("N") int N) {
        return ResponseEntity.ok(adminService.listNDriverDetails(N));
    }

    @GetMapping(value = "/drivers/earnings", produces = "application/json")
    public ResponseEntity<DriverEarningsDTO> getDriverEarnings(@RequestParam("driverID") String driverID) {
        return ResponseEntity.ok(adminService.getDriverEarnings(driverID));
    }
}
