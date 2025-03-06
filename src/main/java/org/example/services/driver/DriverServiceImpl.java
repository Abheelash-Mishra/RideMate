package org.example.services.driver;

import org.example.exceptions.InvalidDriverIDException;
import org.example.repository.Database;
import org.example.models.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DriverServiceImpl implements DriverService {
    private final Database db;

    @Autowired
    public DriverServiceImpl(Database db) {
        this.db = db;
    }

    @Override
    public void addDriver(String driverID, int x_coordinate, int y_coordinate) {
        db.getDriverDetails().put(driverID, new Driver(driverID, x_coordinate, y_coordinate));
    }

    @Override
    public Map<String, Object> rateDriver(String driverID, float rating) {
        Driver driver = db.getDriverDetails().get(driverID);
        if (driver == null) {
            throw new InvalidDriverIDException();
        }

        float updatedRating = updateDriverRating(driverID, rating);

        Map<String, Object> response = new HashMap<>();
        response.put("driverID", driverID);
        response.put("rating", updatedRating);

        return response;
    }

    public float updateDriverRating(String driverID, float newRate) {
        Driver driver = db.getDriverDetails().get(driverID);

        driver.setRidesDone(driver.getRidesDone() + 1);
        driver.setRatingSum(driver.getRatingSum() + newRate);
        driver.setRating(driver.getRatingSum() / driver.getRidesDone());

        return driver.getRating();
    }

    public void updateAvailability(String driverID) {
        Driver driver = db.getDriverDetails().get(driverID);

        driver.setAvailable(!driver.isAvailable());
    }
}
