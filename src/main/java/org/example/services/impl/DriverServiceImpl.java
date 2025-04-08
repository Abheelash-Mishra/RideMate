package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverRatingDTO;
import org.example.exceptions.InvalidDriverIDException;
import org.example.models.Driver;
import org.example.repository.DriverRepository;
import org.example.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class DriverServiceImpl implements DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Override
    public void addDriver(long driverID, String email, String phoneNumber, int x_coordinate, int y_coordinate) {
        Driver driver = new Driver(driverID, email, phoneNumber, x_coordinate, y_coordinate);
        driverRepository.save(driver);

        log.info("Added driver '{}' to database", driverID);
    }

    @Override
    public DriverRatingDTO rateDriver(long driverID, float rating) {
        log.info("Fetching details of driver '{}'...", driverID);

        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException("Invalid driver ID - " +  driverID + ", no such driver exists"));

        driver.setRidesDone(driver.getRidesDone() + 1);
        driver.setRatingSum(driver.getRatingSum() + rating);
        driver.setRating(driver.getRatingSum() / driver.getRidesDone());

        driverRepository.save(driver);

        log.info("Updated the ratings of driver '{}'", driverID);

        return new DriverRatingDTO(driverID, driver.getRating());
    }
}
