package org.example.services;

import org.example.dto.DriverRatingDTO;
import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideDetailsDTO;
import org.example.dto.RideStatusDTO;

import java.util.List;

public interface RideService {
    MatchedDriversDTO matchRider();
    RideStatusDTO startRide(int N, String destination, int destX, int destY);
    RideStatusDTO stopRide(long rideID, int timeTakenInMins);
    double billRide(long rideID);
    DriverRatingDTO rateDriver(long rideID, long driverID, float rating, String comment);
    List<RideDetailsDTO> getAllRides();
}
