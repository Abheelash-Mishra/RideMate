package org.example.services;

import org.example.dto.DriverRatingDTO;

public interface DriverService {
    long addDriver(String email, String phoneNumber, int x_coordinate, int y_coordinate);
    DriverRatingDTO rateDriver(long driverID, float rating);
}
