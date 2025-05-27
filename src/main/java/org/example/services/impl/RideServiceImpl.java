package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverRatingDTO;
import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideDetailsDTO;
import org.example.dto.RideStatusDTO;
import org.example.exceptions.InvalidRiderIDException;
import org.example.models.*;

import org.example.exceptions.InvalidRideException;
import org.example.exceptions.InvalidDriverIDException;
import org.example.repository.DriverRepository;
import org.example.repository.RideRepository;
import org.example.repository.RiderRepository;
import org.example.repository.UserRepository;
import org.example.services.RideService;
import org.example.utilities.DistanceUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private UserRepository userRepository;

    @Override
    public MatchedDriversDTO matchRider() {
        final double LIMIT = 5.0;

        Rider rider = riderRepository.findByUserId(getUserId())
                .orElseThrow(() -> new InvalidRiderIDException("No Such Rider Exists"));

        try {
            log.info("Searching for potential drivers for rider '{}'....", rider.getId());

            List<Long> nearbyDrivers = driverRepository.findNearbyDrivers(rider.getX_coordinate(), rider.getY_coordinate(), LIMIT, PageRequest.of(0, 20));

            return driversMatched(rider, nearbyDrivers);
        } catch (Exception e) {
            log.error("Unexpected error while matching drivers with rider '{}'", rider.getId());
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to match a driver for rider " + rider.getId(), e);
        }
    }


    private MatchedDriversDTO driversMatched(Rider rider, List<Long> nearbyDrivers) {
        if (nearbyDrivers.isEmpty()) {
            log.info("No suitable drivers available for rider '{}'", rider.getId());
        }
        else {
            log.info("Found potential driver(s) for rider '{}'", rider.getId());
            log.info("Drivers: {}", nearbyDrivers);
        }

        rider.setMatchedDrivers(nearbyDrivers);
        riderRepository.save(rider);

        return new MatchedDriversDTO(nearbyDrivers);
    }


    @Override
    @CacheEvict(value = "allRides", key = "#root.target.getUserId()")
    public RideStatusDTO startRide(int N, String destination, int destX, int destY) {
        Rider rider = riderRepository.findByUserId(getUserId())
                .orElseThrow(() -> new InvalidRiderIDException("No such rider"));

        List<Long> matchedDrivers = rider.getMatchedDrivers();

        if (matchedDrivers.isEmpty()) {
            throw new InvalidRideException("No drivers are available around you");
        }

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

            log.info("Ride started for rider '{}' with driver '{}'", rider.getId(), chosenDriverId);

            return new RideStatusDTO(ride.getRideID(), rider.getId(), chosenDriverId, RideStatus.ONGOING);
        } catch (Exception e) {
            log.error("Unexpected error while starting a ride");
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to start ride for rider " + rider.getId(), e);
        }
    }


    @Override
    @CacheEvict(value = "allRides", key = "#root.target.getUserId()")
    public RideStatusDTO stopRide(long rideID, int timeTakenInMins) {
        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(() -> new InvalidRideException("Invalid Ride ID - " + rideID + ", no such ride exists"));

        if (currentRide.getStatus() == RideStatus.FINISHED) {
            throw new InvalidRideException("Invalid Ride Status - " + rideID + ", ride already finished");
        }

        long driverID = currentRide.getDriver().getId();
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException("Invalid Driver ID - " + driverID + ", no such driver exists"));

        try {
            driver.setAvailable(true);
            driverRepository.save(driver);

            currentRide.setTimeTakenInMins(timeTakenInMins);
            currentRide.setStatus(RideStatus.FINISHED);
            rideRepository.save(currentRide);

            log.info("Successfully stopped ride '{}'", rideID);

            return new RideStatusDTO(rideID, currentRide.getRider().getId(), currentRide.getDriver().getId(), RideStatus.FINISHED);
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
    public DriverRatingDTO rateDriver(long rideID, long driverID, float rating, String comment) {
        log.info("Fetching details of driver '{}'...", driverID);

        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException("Invalid driver ID - " +  driverID + ", no such driver exists"));

        driver.setRidesDone(driver.getRidesDone() + 1);
        driver.setRatingSum(driver.getRatingSum() + rating);
        driver.setRating(driver.getRatingSum() / driver.getRidesDone());

        driverRepository.save(driver);

        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(() -> new InvalidRideException("Invalid Ride ID - " + rideID, new NoSuchElementException("Ride does not exist in database")));

        currentRide.setComment(comment);

        rideRepository.save(currentRide);

        log.info("Updated the ratings of driver '{}'", driverID);

        return new DriverRatingDTO(driverID, driver.getRating());
    }

    @Override
    @Cacheable(value = "allRides", key = "#root.target.getUserId()")
    public List<RideDetailsDTO> getAllRides() {
        Rider rider = riderRepository.findByUserId(getUserId())
                .orElseThrow(() -> new InvalidRiderIDException("No such rider"));

        try {
            List<Object[]> rawData = rideRepository.findAllRides(rider.getId());
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
            log.error("Unexpected error while fetching all rides of rider '{}'", rider.getId());
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to fetch all rides of rider " + rider.getId(), e);
        }
    }

    public long getUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        return user.getId();
    }
}
