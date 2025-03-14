package org.example.controllers;


import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideStatusDTO;
import org.example.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    private RideService rideService;

    @PostMapping("/rider/add")
    public ResponseEntity<String> addRider(
            @RequestParam("riderID") long riderID,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        rideService.addRider(riderID, x, y);
        return ResponseEntity.status(HttpStatus.CREATED).body("Rider Added!");
    }

    @GetMapping("/rider/match")
    public ResponseEntity<MatchedDriversDTO> matchRider(@RequestParam("riderID") long riderID) {
        return ResponseEntity.ok(rideService.matchRider(riderID));
    }

    @PostMapping("/start")
    public ResponseEntity<RideStatusDTO> startRide(
            @RequestParam("rideID") long rideID,
            @RequestParam("N") int N,
            @RequestParam("riderID") long riderID
    ) {
        return ResponseEntity.ok(rideService.startRide(rideID, N, riderID));
    }

    @PostMapping("/stop")
    public ResponseEntity<RideStatusDTO> stopRide(
            @RequestParam("rideID") long rideID,
            @RequestParam("x") int x,
            @RequestParam("y") int y,
            @RequestParam("timeInMins") int timeInMins
    ) {
        return ResponseEntity.ok(rideService.stopRide(rideID, x, y, timeInMins));
    }

    @GetMapping("/bill")
    public Double billRide(@RequestParam("rideID") long rideID) {
        return rideService.billRide(rideID);
    }
}
