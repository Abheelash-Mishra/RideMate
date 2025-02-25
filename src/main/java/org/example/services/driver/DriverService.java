package org.example.services.driver;

public interface DriverService {
    void addDriver(String driverID, int x_coordinate, int y_coordinate);
    String rateDriver(String driverID, float rating);
}
