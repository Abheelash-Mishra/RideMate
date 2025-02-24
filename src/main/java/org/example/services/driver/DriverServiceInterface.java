package org.example.services.driver;

public interface DriverServiceInterface {
    void addDriver(String driverID, int x_coordinate, int y_coordinate);
    void rateDriver(String driverID, float rating);
}
