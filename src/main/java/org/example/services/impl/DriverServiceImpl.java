package org.example.services.impl;

import org.example.dto.DriverRatingDTO;
import org.example.exceptions.InvalidDriverIDException;
import org.example.models.Driver;
import org.example.repository.DriverRepository;
import org.example.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverServiceImpl implements DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Override
    public void addDriver(String driverID, int x_coordinate, int y_coordinate) {
        Driver driver = new Driver(driverID, x_coordinate, y_coordinate);

        driverRepository.save(driver);
    }

    @Override
    public DriverRatingDTO rateDriver(String driverID, float rating) {
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(InvalidDriverIDException::new);

        driver.setRidesDone(driver.getRidesDone() + 1);
        driver.setRatingSum(driver.getRatingSum() + rating);
        driver.setRating(driver.getRatingSum() / driver.getRidesDone());

        driverRepository.save(driver);

        return new DriverRatingDTO(driverID, driver.getRating());
    }
}
