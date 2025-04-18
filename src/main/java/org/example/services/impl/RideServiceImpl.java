package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.MatchedDriversDTO;
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
import org.springframework.stereotype.Service;

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

        List<Driver> allDrivers = driverRepository.findAll();

        PriorityQueue<DriverDistancePair> nearestDrivers = new PriorityQueue<>((pair1, pair2) -> {
            if (pair1.distance < pair2.distance) {
                return -1;
            } else if (pair1.distance > pair2.distance) {
                return 1;
            }

            if (pair1.ID < pair2.ID) {
                return -1;
            } else if (pair1.ID > pair2.ID) {
                return 1;
            }

            return 0;
        });

        try {
            log.info("Searching for potential drivers for rider '{}'....", riderID);
            for (Driver driver : allDrivers) {
                if (driver.isAvailable()) {
                    double distance = DistanceUtility.calculate(rider.getCoordinates(), driver.getCoordinates());
                    if (distance <= LIMIT) {
                        nearestDrivers.add(new DriverDistancePair(driver.getDriverID(), distance));
                    }
                }
            }

            return driversMatched(rider, nearestDrivers);
        } catch (Exception e) {
            log.error("Unexpected error while matching drivers with rider '{}'", riderID);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to match a driver for rider  " + riderID, e);
        }
    }


    private MatchedDriversDTO driversMatched(Rider rider, PriorityQueue<DriverDistancePair> nearestDrivers) {
        if (nearestDrivers.isEmpty()) {
            return new MatchedDriversDTO(Collections.emptyList());
        }

        List<Long> matchedDrivers = new ArrayList<>();
        int size = Math.min(nearestDrivers.size(), 5);

        for (int i = 0; i < size; i++) {
            matchedDrivers.add(Objects.requireNonNull(nearestDrivers.poll()).ID);
        }

        rider.setMatchedDrivers(matchedDrivers);
        riderRepository.save(rider);

        if (!matchedDrivers.isEmpty()) {
            log.info("Found potential driver(s) for rider '{}'", rider.getRiderID());
        }
        else {
            log.info("No suitable drivers available for rider '{}'", rider.getRiderID());
        }

        return new MatchedDriversDTO(matchedDrivers);
    }


    @Override
    public RideStatusDTO startRide(int N, long riderID, String destination, int destX, int destY) {
        Rider rider = riderRepository.findById(riderID)
                .orElseThrow(() -> new InvalidRiderIDException("Invalid Rider ID - " + riderID + ", no such rider exists"));

        List<Long> matchedDrivers = rider.getMatchedDrivers();

        if (matchedDrivers.size() < N) {
            throw new InvalidRideException("Invalid Ride", new ArrayIndexOutOfBoundsException("User requested for a driver that does not exist in the array"));
        }

        long driverID = matchedDrivers.get(N - 1);
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException("Invalid Driver ID - " + driverID + ", no such driver exists"));

        if (!driver.isAvailable()) {
            throw new InvalidRideException("Invalid Ride, driver is already preoccupied with another ride");
        }

        try {
            driver.setAvailable(false);
            driverRepository.save(driver);

            rider.setMatchedDrivers(Collections.emptyList());
            riderRepository.save(rider);

            Ride currentRide = new Ride(rider, driver);

            // Code breaks because the ride does not save if we don't use new ArrayList<>
            currentRide.setDestinationCoordinates(new ArrayList<>(List.of(destX, destY)));
            currentRide.setDestination(destination);
            rideRepository.save(currentRide);

            log.info("Successfully started a ride for rider '{}' with driver '{}'", riderID, driverID);

            long rideID = currentRide.getRideID();

            return new RideStatusDTO(rideID, riderID, driverID, RideStatus.ONGOING);
        } catch (Exception e) {
            log.error("Unexpected error while starting a ride");
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to start a ride for rider " + riderID, e);
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

            List<Integer> startCoordinates = currentRide.getRider().getCoordinates();
            List<Integer> destCoordinates = currentRide.getDestinationCoordinates();
            double distanceTravelled = DistanceUtility.calculate(startCoordinates, destCoordinates);
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

    public record DriverDistancePair(long ID, double distance) {
    }
}
