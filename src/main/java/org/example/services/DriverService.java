package org.example.services;

import org.example.dto.DriverRatingDTO;

public interface DriverService {
    void addDriver(String driverID, int x_coordinate, int y_coordinate);
    DriverRatingDTO rateDriver(String driverID, float rating);
}
