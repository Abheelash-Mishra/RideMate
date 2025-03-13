package org.example.services.impl;

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

@Service
public class RideServiceImpl implements RideService {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public void addRider(String riderID, int x_coordinate, int y_coordinate) {
        Rider rider = new Rider(riderID, x_coordinate, y_coordinate);

        riderRepository.save(rider);
    }

    @Override
    public MatchedDriversDTO matchRider(String riderID) {
        final double LIMIT = 5.0;

        Rider rider = riderRepository.findById(riderID)
                .orElseThrow(InvalidRiderIDException::new);

        List<Driver> allDrivers = driverRepository.findAll();

        PriorityQueue<DriverDistancePair> nearestDrivers = new PriorityQueue<>((pair1, pair2) -> {
            if (pair1.distance < pair2.distance) {
                return -1;
            } else if (pair1.distance > pair2.distance) {
                return 1;
            }

            return pair1.ID.compareTo(pair2.ID);
        });

        for (Driver driver : allDrivers) {
            if (driver.isAvailable()) {
                double distance = DistanceUtility.calculate(rider.getCoordinates(), driver.getCoordinates());
                if (distance <= LIMIT) {
                    nearestDrivers.add(new DriverDistancePair(driver.getDriverID(), distance));
                }
            }
        }

        return driversMatched(riderID, nearestDrivers);
    }


    private MatchedDriversDTO driversMatched(String riderID, PriorityQueue<DriverDistancePair> nearestDrivers) {
        if (nearestDrivers.isEmpty()) {
            return new MatchedDriversDTO(Collections.emptyList());
        }

        List<String> matchedDrivers = new ArrayList<>();
        int size = Math.min(nearestDrivers.size(), 5);

        for (int i = 0; i < size; i++) {
            matchedDrivers.add(Objects.requireNonNull(nearestDrivers.poll()).ID);
        }

        Rider rider = riderRepository.findById(riderID)
                .orElseThrow(InvalidRiderIDException::new);

        rider.setMatchedDrivers(matchedDrivers);
        riderRepository.save(rider);

        return new MatchedDriversDTO(matchedDrivers);
    }


    @Override
    public RideStatusDTO startRide(String rideID, int N, String riderID) {
        Rider rider = riderRepository.findById(riderID)
                .orElseThrow(InvalidRiderIDException::new);

        List<String> matchedDrivers = rider.getMatchedDrivers();

        if (matchedDrivers.size() < N) {
            throw new InvalidRideException();
        }

        String driverID = matchedDrivers.get(N - 1);
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException(driverID));

        if (!driver.isAvailable() || rideRepository.existsById(rideID)) {
            throw new InvalidRideException();
        }

        driver.setAvailable(false);
        driverRepository.save(driver);

        rider.setMatchedDrivers(Collections.emptyList());
        riderRepository.save(rider);

        Ride ride = new Ride(rideID, rider, driver);
        rideRepository.save(ride);

        return new RideStatusDTO(rideID, riderID, driverID, RideStatus.ONGOING);
    }


    @Override
    public RideStatusDTO stopRide(String rideID, int destX, int destY, int timeTakenInMins) {
        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(InvalidRideException::new);

        if (currentRide.getStatus() == RideStatus.FINISHED) {
            throw new InvalidRideException();
        }

        // Fetch driver and update availability
        String driverID = currentRide.getDriver().getDriverID();
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException(driverID));

        driver.setAvailable(true);
        driverRepository.save(driver);

        currentRide.setDestinationCoordinates(new ArrayList<>(List.of(destX, destY)));
        currentRide.setTimeTakenInMins(timeTakenInMins);
        currentRide.setStatus(RideStatus.FINISHED);
        rideRepository.save(currentRide);

        return new RideStatusDTO(rideID, currentRide.getRider().getRiderID(), currentRide.getDriver().getDriverID(), RideStatus.FINISHED);
    }


    @Override
    public double billRide(String rideID) {
        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(InvalidRideException::new);

        if (currentRide.getStatus() != RideStatus.FINISHED) {
            throw new InvalidRideException();
        }

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
        return currentRide.getBill();
    }

    public record DriverDistancePair(String ID, double distance) {
    }
}
