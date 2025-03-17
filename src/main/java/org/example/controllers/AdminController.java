package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverDTO;
import org.example.dto.DriverEarningsDTO;
import org.example.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @DeleteMapping("/drivers/remove")
    public ResponseEntity<Boolean> removeDriver(@RequestParam("driverID") long driverID) {
        log.info("Accessing endpoint: /admin/drivers/remove | driverID={}", driverID);

        return ResponseEntity.ok(adminService.removeDriver(driverID));
    }

    @GetMapping("/drivers/list")
    public ResponseEntity<List<DriverDTO>> listNDriverDetails(@RequestParam("N") int N) {
        log.info("Accessing endpoint: /admin/drivers/list | N={}", N);

        try {
            List<DriverDTO> response = adminService.listNDriverDetails(N);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Could not list drivers unexpectedly | Exception: {}", e.getMessage(), e);

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/drivers/earnings")
    public ResponseEntity<DriverEarningsDTO> getDriverEarnings(@RequestParam("driverID") long driverID) {
        log.info("Accessing endpoint: /admin/drivers/earnings | driverID={}", driverID);

        return ResponseEntity.ok(adminService.getDriverEarnings(driverID));
    }
}
