package services;

import database.InMemoryDB;
import models.Driver;

public class DriverService {
    private final InMemoryDB db;

    public DriverService(InMemoryDB db) {
        this.db = db;
    }

    public void addDriver(String driverID, int x_coordinate, int y_coordinate) {
        db.driverDetails.put(driverID, new Driver(x_coordinate, y_coordinate));
    }

    public void rateDriver(String driverID, float rating) {
        Driver driver = db.driverDetails.get(driverID);

        System.out.printf("CURRENT_RATING %s %.1f%n", driverID, driver.updateDriverRating(rating));
    }
}
