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
        db.getDriverDetails().put(driverID, new Driver(x_coordinate, y_coordinate));
    }

    @Override
    public Map<String, Object> rateDriver(String driverID, float rating) {
        Driver driver = db.getDriverDetails().get(driverID);
        if (driver == null) {
            throw new InvalidDriverIDException();
        }

        float updatedRating = driver.updateDriverRating(rating);

        Map<String, Object> response = new HashMap<>();
        response.put("driverID", driverID);
        response.put("rating", updatedRating);

        return response;
    }

}
