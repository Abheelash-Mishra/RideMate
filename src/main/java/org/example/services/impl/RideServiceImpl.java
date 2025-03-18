package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideStatusDTO;
import org.example.exceptions.InvalidRiderIDException;
import org.example.exceptions.RecordAlreadyExistsException;
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
import org.springframework.dao.DataIntegrityViolationException;
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
    public void addRider(long riderID, int x_coordinate, int y_coordinate) {
        try {
            if (riderRepository.existsById(riderID)) {
                log.warn("Rider with ID '{}' already exists in the database", riderID);

                throw new RecordAlreadyExistsException("Duplicate rider ID - " + riderID,
                        new DataIntegrityViolationException("Rider already exists"));
            }

            Rider rider = new Rider(riderID, x_coordinate, y_coordinate);
            riderRepository.save(rider);

            log.info("Added rider '{}' to database", riderID);
        } catch (RecordAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding new rider | riderID={} | Error: {}", riderID, e.getMessage(), e);
            throw new RuntimeException("Failed to add rider with ID " + riderID, e);
        }
    }


    @Override
    public MatchedDriversDTO matchRider(long riderID) {
        try {
            final double LIMIT = 5.0;

            Rider rider = riderRepository.findById(riderID)
                    .orElseThrow(() -> new InvalidRiderIDException(riderID, new NoSuchElementException("Rider not present in database")));

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
            log.error("Unexpected error while matching drivers | riderID={} | Error: {}", riderID, e.getMessage(), e);
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
    public RideStatusDTO startRide(long rideID, int N, long riderID) {
        Rider rider = riderRepository.findById(riderID)
                .orElseThrow(() -> new InvalidRiderIDException(riderID, new NoSuchElementException("Rider not present in database")));

        List<Long> matchedDrivers = rider.getMatchedDrivers();

        if (matchedDrivers.size() < N) {
            throw new InvalidRideException(rideID, new ArrayIndexOutOfBoundsException("User requested for a driver that does not exist in the array"));
        }

        long driverID = matchedDrivers.get(N - 1);
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException(driverID, new NoSuchElementException("Driver not present in database")));

        if (!driver.isAvailable()) {
            throw new InvalidRideException(rideID, new UnsupportedOperationException("Driver is already preoccupied with another ride"));
        }

        if (rideRepository.existsById(rideID)) {
            throw new InvalidRideException(rideID, new IllegalStateException("Ride already exists"));
        }

        try {
            driver.setAvailable(false);
            driverRepository.save(driver);

            rider.setMatchedDrivers(Collections.emptyList());
            riderRepository.save(rider);

            Ride ride = new Ride(rideID, rider, driver);
            rideRepository.save(ride);

            log.info("Successfully started a ride for rider '{}' with driver '{}'", riderID, driverID);

            return new RideStatusDTO(rideID, riderID, driverID, RideStatus.ONGOING);
        } catch (Exception e) {
            log.error("Unexpected error while starting a ride | Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to start a ride for rider " + riderID, e);
        }
    }


    @Override
    public RideStatusDTO stopRide(long rideID, int destX, int destY, int timeTakenInMins) {
        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(() -> new InvalidRideException(rideID, new NoSuchElementException("Ride does not exist in database")));

        if (currentRide.getStatus() == RideStatus.FINISHED) {
            throw new InvalidRideException(rideID, new IllegalStateException("Ride already finished"));
        }

        long driverID = currentRide.getDriver().getDriverID();
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException(driverID, new NoSuchElementException("Driver not present in database")));

        try {
            driver.setAvailable(true);
            driverRepository.save(driver);

            currentRide.setDestinationCoordinates(new ArrayList<>(List.of(destX, destY)));
            currentRide.setTimeTakenInMins(timeTakenInMins);
            currentRide.setStatus(RideStatus.FINISHED);
            rideRepository.save(currentRide);

            log.info("Successfully stopped ride '{}'", rideID);

            return new RideStatusDTO(rideID, currentRide.getRider().getRiderID(), currentRide.getDriver().getDriverID(), RideStatus.FINISHED);
        } catch (Exception e) {
            log.error("Unexpected error while stopping ride '{}' | Error: {}", rideID, e.getMessage(), e);
            throw new RuntimeException("Failed to stop ride " + rideID, e);
        }
    }


    @Override
    public double billRide(long rideID) {
        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(() -> new InvalidRideException(rideID, new NoSuchElementException("Ride does not exist in database")));

        if (currentRide.getStatus() != RideStatus.FINISHED) {
            throw new InvalidRideException(rideID, new IllegalStateException("Cannot bill a ride that has not finished"));
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
            log.error("Unexpected error while generating bill for ride '{}' | Error: {}", rideID, e.getMessage(), e);
            throw new RuntimeException("Failed to generate bill for ride " + rideID, e);
        }
    }

    public record DriverDistancePair(long ID, double distance) {
    }
}
