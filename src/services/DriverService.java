package services;

import models.Driver;

import java.util.HashMap;

public class DriverService {
    public HashMap<String, Driver> driverDetails = new HashMap<>();

    public void addDriver(String driverID, int x_coordinate, int y_coordinate) {
        driverDetails.put(driverID, new Driver(driverID, x_coordinate, y_coordinate));
    }

    public void rateDriver(String driverID, float rating) {
        Driver driver = driverDetails.get(driverID);

        System.out.println("CURRENT_RATING " + driverID + " " + driver.updateDriverRating(rating));
    }
}
