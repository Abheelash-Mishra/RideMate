package org.example.services;

import org.example.dto.DriverRatingDTO;

public interface DriverService {
    void addDriver(long driverID, int x_coordinate, int y_coordinate);
    DriverRatingDTO rateDriver(long driverID, float rating);
}
