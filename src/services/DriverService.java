package services;

import database.Database;

import models.Driver;

public class DriverService {
    private final Database db;

    public DriverService(Database db) {
        this.db = db;
    }

    public void addDriver(String driverID, int x_coordinate, int y_coordinate) {
        db.getDriverDetails().put(driverID, new Driver(x_coordinate, y_coordinate));
    }

    public void rateDriver(String driverID, float rating) {
        Driver driver = db.getDriverDetails().get(driverID);

        System.out.printf("CURRENT_RATING %s %.1f%n", driverID, driver.updateDriverRating(rating));
    }
}
