package org.example.services.driver;

import org.example.database.Database;
import org.example.models.Driver;

public class DriverServiceImpl implements DriverService {
    private final Database db;

    public DriverServiceImpl(Database db) {
        this.db = db;
    }

    @Override
    public void addDriver(String driverID, int x_coordinate, int y_coordinate) {
        db.getDriverDetails().put(driverID, new Driver(x_coordinate, y_coordinate));
    }

    @Override
    public void rateDriver(String driverID, float rating) {
        Driver driver = db.getDriverDetails().get(driverID);

        System.out.printf("CURRENT_RATING %s %.1f%n", driverID, driver.updateDriverRating(rating));
    }
}
