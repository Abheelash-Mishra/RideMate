package org.example.controllers;


import org.example.models.Ride;
import org.example.repository.Database;
import org.example.services.ride.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    private RideService rideService;

    @GetMapping("/rider/add")
    public void addRider(
            @RequestParam("riderID") String riderID,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        rideService.addRider(riderID, x, y);
    }

    @GetMapping("/rider/match")
    public String matchRider(@RequestParam("riderID") String riderID) {
        return rideService.matchRider(riderID);
    }

    @GetMapping("/start")
    public String startRide(
            @RequestParam("rideID") String rideID,
            @RequestParam("N") int N,
            @RequestParam("riderID") String riderID
    ) {
        return rideService.startRide(rideID, N, riderID);
    }

    @GetMapping("/stop")
    public String stopRide(
            @RequestParam("rideID") String rideID,
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam("timeInMins") int timeInMins
    ) {
        return rideService.stopRide(rideID, x, y, timeInMins);
    }

    @GetMapping("/bill")
    public String billRide(@RequestParam("rideID") String rideID) {
        double bill = rideService.billRide(rideID);

        return String.format("BILL %s %.1f\n", rideID, bill);
    }
}
