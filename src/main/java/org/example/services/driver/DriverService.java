package org.example.services.driver;

import java.util.Map;

public interface DriverService {
    void addDriver(String driverID, int x_coordinate, int y_coordinate);
    Map<String, Object> rateDriver(String driverID, float rating);
}
