package org.example.services.driver.impl;

import org.example.database.Database;
import org.example.models.Driver;
import org.example.services.driver.DriverServiceInterface;

public class DriverServiceConsoleImpl implements DriverServiceInterface {
    private final Database db;

    public DriverServiceConsoleImpl(Database db) {
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
