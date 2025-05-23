package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverRatingDTO;
import org.example.exceptions.InvalidDriverIDException;
import org.example.exceptions.InvalidRideException;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.repository.DriverRepository;
import org.example.repository.RideRepository;
import org.example.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;


@Slf4j
@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Override
    public long addDriver(String email, String phoneNumber, int x_coordinate, int y_coordinate) {
        try {
            Driver driver = new Driver(email, phoneNumber, x_coordinate, y_coordinate);
            driverRepository.save(driver);

            long driverID = driver.getDriverID();
            log.info("Added driver '{}' to database", driverID);

            return driverID;
        } catch (Exception e) {
            log.error("Service failed to add driver to the database");
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to add driver", e);
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
}
