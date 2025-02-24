package org.example.services.driver.impl;

import org.example.database.Database;
import org.example.services.driver.DriverServiceInterface;


public class DriverServiceRestImpl implements DriverServiceInterface {
    private final Database db;

    public DriverServiceRestImpl(Database db) {
        this.db = db;
    }

    @Override
    public void addDriver(String driverID, int x_coordinate, int y_coordinate) {
        System.out.println("ADDING DRIVER USING REST IMPL");
    }

    @Override
    public void rateDriver(String driverID, float rating) {
        System.out.println("RATING DRIVER USING REST IMPL");
    }
}
