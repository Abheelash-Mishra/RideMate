package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverRatingDTO;
import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideDetailsDTO;
import org.example.dto.RideStatusDTO;
import org.example.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    private RideService rideService;

    @GetMapping("/rider/match")
    public ResponseEntity<MatchedDriversDTO> matchRider() {
        log.info("Accessing endpoint: /ride/rider/match");

        return ResponseEntity.ok(rideService.matchRider());
    }

    @PostMapping("/start")
    public ResponseEntity<RideStatusDTO> startRide(
            @RequestParam("N") int N,
            @RequestParam("destination") String destination,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        log.info("Accessing endpoint: /ride/start || PARAMS: N={}, destination={}, x={}, y={}", N, destination, x, y);

        return ResponseEntity.ok(rideService.startRide(N, destination, x, y));
    }

    @PostMapping("/stop")
    public ResponseEntity<RideStatusDTO> stopRide(
            @RequestParam("rideID") long rideID,
            @RequestParam("timeInMins") int timeInMins
    ) {
        log.info("Accessing endpoint: /ride/stop || PARAMS: rideID={}, timeInMins={}", rideID, timeInMins);

        return ResponseEntity.ok(rideService.stopRide(rideID, timeInMins));
    }

    @GetMapping("/bill")
    public Double billRide(@RequestParam("rideID") long rideID) {
        log.info("Accessing endpoint: /ride/bill || PARAMS: rideID={}", rideID);

        return rideService.billRide(rideID);
    }

    @PostMapping("/rate")
    public ResponseEntity<DriverRatingDTO> rateDriver(
            @RequestParam("rideID") long rideID,
            @RequestParam("driverID") long driverID,
            @RequestParam("rating") float rating,
            @RequestParam("comment") String comment
    ) {
        log.info("Accessing endpoint: /driver/rate || PARAMS: driverID={}, rating={}, comment='{}'", driverID, rating, comment);

        return ResponseEntity.ok(rideService.rateDriver(rideID, driverID, rating, comment));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RideDetailsDTO>> getAllRides() {
        log.info("Accessing endpoint: /ride/all");

        return ResponseEntity.ok(rideService.getAllRides());
    }
}
