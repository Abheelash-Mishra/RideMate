package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideDetailsDTO;
import org.example.dto.RideStatusDTO;
import org.example.exceptions.InvalidRiderIDException;
import org.example.models.RideStatus;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.models.Rider;

import org.example.exceptions.InvalidRideException;
import org.example.exceptions.InvalidDriverIDException;
import org.example.repository.DriverRepository;
import org.example.repository.RideRepository;
import org.example.repository.RiderRepository;
import org.example.services.RideService;
import org.example.utilities.DistanceUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class RideServiceImpl implements RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public long addRider(String email, String phoneNumber, int x_coordinate, int y_coordinate) {
        try {
            Rider rider = new Rider(email, phoneNumber, x_coordinate, y_coordinate);
            riderRepository.save(rider);

            long riderID = rider.getRiderID();
            log.info("Added rider '{}' to database", riderID);

            return riderID;
        } catch (Exception e) {
            log.error("Unexpected error while adding new rider");
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to add rider", e);
        }
    }


    @Override
    public MatchedDriversDTO matchRider(long riderID) {
        final double LIMIT = 5.0;

        Rider rider = riderRepository.findById(riderID)
                .orElseThrow(() -> new InvalidRiderIDException("Invalid Rider ID - " + riderID + " || No Such Rider Exists"));

        try {
            log.info("Searching for potential drivers for rider '{}'....", riderID);

            List<Long> nearbyDrivers = driverRepository.findNearbyDrivers(rider.getX_coordinate(), rider.getY_coordinate(), LIMIT, PageRequest.of(0, 100));

            return driversMatched(rider, nearbyDrivers);
        } catch (Exception e) {
            log.error("Unexpected error while matching drivers with rider '{}'", riderID);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to match a driver for rider  " + riderID, e);
        }
    }


    private MatchedDriversDTO driversMatched(Rider rider, List<Long> nearbyDrivers) {
        if (nearbyDrivers.isEmpty()) {
            log.info("No suitable drivers available for rider '{}'", rider.getRiderID());

            return new MatchedDriversDTO(Collections.emptyList());
        }

        rider.setMatchedDrivers(nearbyDrivers);
        riderRepository.save(rider);

        log.info("Found potential driver(s) for rider '{}'", rider.getRiderID());
        log.info("Drivers: {}", nearbyDrivers);

        return new MatchedDriversDTO(nearbyDrivers);
    }


    @Override
    public RideStatusDTO startRide(int N, long riderID, String destination, int destX, int destY) {
        Rider rider = riderRepository.findById(riderID)
                .orElseThrow(() -> new InvalidRiderIDException("Invalid Rider ID - " + riderID));

        List<Long> matchedDrivers = rider.getMatchedDrivers();
        if (matchedDrivers.size() < N) {
            throw new InvalidRideException("Requested driver index " + N + " out of bounds");
        }

        Long chosenDriverId = null;
        List<Long> rotation = new ArrayList<>();

        rotation.addAll(matchedDrivers.subList(N - 1, matchedDrivers.size()));
        rotation.addAll(matchedDrivers.subList(0, N - 1));

        for (Long driverId : rotation) {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new InvalidDriverIDException("Invalid Driver ID - " + driverId));

            if (driver.isAvailable()) {
                chosenDriverId = driverId;
                driver.setAvailable(false);
                driverRepository.save(driver);
                break;
            }
        }

        if (chosenDriverId == null) {
            rider.setMatchedDrivers(Collections.emptyList());
            riderRepository.save(rider);

            throw new InvalidRideException("All matched drivers are currently busy");
        }

        try {
            rider.setMatchedDrivers(Collections.emptyList());
            riderRepository.save(rider);

            Long finalChosenDriverId = chosenDriverId;
            Driver driver = driverRepository.findById(finalChosenDriverId)
                    .orElseThrow(() -> new InvalidDriverIDException("Invalid Driver ID - " + finalChosenDriverId));

            Ride ride = new Ride(rider, driver);
            ride.setDestination(destination);
            ride.setDestinationCoordinates(new ArrayList<>(List.of(destX, destY)));
            rideRepository.save(ride);

            log.info("Ride started for rider {} with driver {}", riderID, chosenDriverId);

            return new RideStatusDTO(ride.getRideID(), riderID, chosenDriverId, RideStatus.ONGOING);
        } catch (Exception e) {
            log.error("Unexpected error while starting a ride");
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to start ride for rider " + riderID, e);
        }
    }


    @Override
    public RideStatusDTO stopRide(long rideID, int timeTakenInMins) {
        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(() -> new InvalidRideException("Invalid Ride ID - " + rideID + ", no such ride exists"));

        if (currentRide.getStatus() == RideStatus.FINISHED) {
            throw new InvalidRideException("Invalid Ride Status - " + rideID + ", ride already finished");
        }

        long driverID = currentRide.getDriver().getDriverID();
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException("Invalid Driver ID - " + driverID + ", no such driver exists"));

        try {
            driver.setAvailable(true);
            driverRepository.save(driver);

            currentRide.setTimeTakenInMins(timeTakenInMins);
            currentRide.setStatus(RideStatus.FINISHED);
            rideRepository.save(currentRide);

            log.info("Successfully stopped ride '{}'", rideID);

            return new RideStatusDTO(rideID, currentRide.getRider().getRiderID(), currentRide.getDriver().getDriverID(), RideStatus.FINISHED);
        } catch (Exception e) {
            log.error("Unexpected error while stopping ride '{}'", rideID);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to stop ride " + rideID, e);
        }
    }


    @Override
    @Transactional
    public double billRide(long rideID) {
        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(() -> new InvalidRideException("Invalid Ride ID - " + rideID, new NoSuchElementException("Ride does not exist in database")));

        if (currentRide.getStatus() != RideStatus.FINISHED) {
            throw new InvalidRideException("Invalid Ride ID - " + rideID, new IllegalStateException("Cannot bill a ride that has not finished"));
        }

        try {
            final double BASE_FARE = 50.0;
            final double PER_KM = 6.5;
            final double PER_MIN = 2.0;
            final double SERVICE_TAX = 1.2;      // 20 percent

            double finalBill = BASE_FARE;

            List<Integer> destCoordinates = currentRide.getDestinationCoordinates();
            double distanceTravelled = DistanceUtility.calculate(currentRide.getRider().getX_coordinate(), currentRide.getRider().getY_coordinate(), destCoordinates.get(0), destCoordinates.get(1));
            finalBill += (distanceTravelled * PER_KM);

            int timeTakenInMins = currentRide.getTimeTakenInMins();
            finalBill += (timeTakenInMins * PER_MIN);

            finalBill *= SERVICE_TAX;
            finalBill = Math.round(finalBill * 10.0) / 10.0;

            currentRide.setBill((float) finalBill);
            rideRepository.save(currentRide);
            log.info("Bill generated for ride '{}' - Rs. {}", rideID, currentRide.getBill());

            return currentRide.getBill();
        } catch (Exception e) {
            log.error("Unexpected error while generating bill for ride '{}'", rideID);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to generate bill for ride " + rideID, e);
        }
    }

    @Override
    public List<RideDetailsDTO> getAllRides(long riderID) {
        try {
            List<Object[]> rawData = rideRepository.findAllRides(riderID);
            List<RideDetailsDTO> summaryList = new ArrayList<>();

            for (Object[] row : rawData) {
                RideDetailsDTO dto = new RideDetailsDTO(
                        (Long) row[0],
                        (Long) row[1],
                        (String) row[2],
                        (Float) row[3],
                        (Integer) row[4],
                        row[5].toString()
                );
                summaryList.add(dto);
            }

            return summaryList;
        } catch (Exception e) {
            log.error("Unexpected error while fetching all rides of rider '{}'", riderID);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to fetch all rides of rider " + riderID, e);
        }
    }
}
