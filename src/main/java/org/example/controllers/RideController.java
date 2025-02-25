package org.example.controllers;


import org.example.models.Ride;
import org.example.repository.Database;
import org.example.services.ride.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public class RideController {
    private final Database db;

    @Autowired
    private RideService rideService;

    @Autowired
    public RideController(Database db) {
        this.db = db;
    }

    @GetMapping("/rider/add")
    @ResponseBody
    public void addRider(@RequestParam String riderID, @RequestParam int x, @RequestParam int y) {
        rideService.addRider(riderID, x, y);
    }

    @GetMapping("/rider/match")
    @ResponseBody
    public String matchRider(@RequestParam String riderID) {
        return rideService.matchRider(riderID);
    }

    @GetMapping("/ride/start")
    @ResponseBody
    public String startRide(@RequestParam String rideID, @RequestParam int N, @RequestParam String riderID) {
        return rideService.startRide(rideID, N, riderID);
    }

    @GetMapping("/ride/stop")
    @ResponseBody
    public String stopRide(@RequestParam String rideID, @RequestParam int x, @RequestParam int y, @RequestParam int timeInMins) {
        return rideService.stopRide(rideID, x, y, timeInMins);
    }

    @GetMapping("/ride/bill")
    @ResponseBody
    public String billRide(@RequestParam String rideID) {
        double bill = rideService.billRide(rideID);
        Ride currentRide = db.getRideDetails().get(rideID);

        return String.format("BILL %s %s %.1f\n", rideID, currentRide.getDriverID(), bill);
    }
}
