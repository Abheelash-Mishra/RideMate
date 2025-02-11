package services;

import models.Driver;

import java.util.HashMap;

public class DriverService {
    private HashMap<String, Driver> driverDetails = new HashMap<>();

    public void addDriver(String driverID, int x_coordinate, int y_coordinate) {
        driverDetails.put(driverID, new Driver(driverID, x_coordinate, y_coordinate));

        System.out.println("Added new driver - " + driverID);
    }
}
