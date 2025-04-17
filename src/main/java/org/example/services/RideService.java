package org.example.services;

import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideDetailsDTO;
import org.example.dto.RideStatusDTO;

import java.util.List;

public interface RideService {
    long addRider(String email, String phoneNumber, int x_coordinate, int y_coordinate);
    MatchedDriversDTO matchRider(long riderID);
    RideStatusDTO startRide(int N, long riderID, String destination, int destX, int destY);
    RideStatusDTO stopRide(long rideID, int timeTakenInMins);
    double billRide(long rideID);
    List<RideDetailsDTO> getAllRides(long riderID);
}
