package org.example.controllers;

import org.example.services.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/drivers/remove")
    @ResponseBody
    public String removeDriver(@RequestParam String driverID) {
        return adminService.removeDriver(driverID);
    }

    @GetMapping("/admin/drivers/list")
    @ResponseBody
    public List<String> listNDrivers(@RequestParam int N) {
        return adminService.listNDriverDetails(N);
    }

    @GetMapping("/admin/drivers/earnings")
    @ResponseBody
    public float getDriverEarnings(@RequestParam String driverID) {
        return adminService.getDriverEarnings(driverID);
    }
}
