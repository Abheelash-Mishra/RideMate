package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideDetailsDTO;
import org.example.dto.RideStatusDTO;
import org.example.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    private RideService rideService;

    @PostMapping("/rider/add")
    public ResponseEntity<Long> addRider(
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        log.info("Accessing endpoint: /ride/rider/add");

        String emailRegex = "^[a-zA-Z0-9_.±]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if (!Pattern.matches(emailRegex, email)) {
            log.warn("Invalid email ID was used. Rider was not registered");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1L);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.addRider(email, phoneNumber, x, y));
    }

    @GetMapping("/rider/match")
    public ResponseEntity<MatchedDriversDTO> matchRider(@RequestParam("riderID") long riderID) {
        log.info("Accessing endpoint: /ride/rider/match || PARAMS: riderID={}", riderID);

        return ResponseEntity.ok(rideService.matchRider(riderID));
    }

    @PostMapping("/start")
    public ResponseEntity<RideStatusDTO> startRide(
            @RequestParam("N") int N,
            @RequestParam("riderID") long riderID,
            @RequestParam("destination") String destination,
            @RequestParam("x") int x,
            @RequestParam("y") int y
    ) {
        log.info("Accessing endpoint: /ride/start || PARAMS: N={}, riderID={}, destination={}, x={}, y={}", N, riderID, destination, x, y);

        return ResponseEntity.ok(rideService.startRide(N, riderID, destination, x, y));
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

    @GetMapping("/all")
    public ResponseEntity<List<RideDetailsDTO>> getAllRides(@RequestParam("riderID") long riderID) {
        log.info("Accessing endpoint: /ride/all || PARAMS: riderID={}", riderID);

        return ResponseEntity.ok(rideService.getAllRides(riderID));
    }
}
