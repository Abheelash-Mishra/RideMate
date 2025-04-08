package org.example.services;

import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideStatusDTO;

public interface RideService {
    long addRider(String email, String phoneNumber, int x_coordinate, int y_coordinate);
    MatchedDriversDTO matchRider(long riderID);
    RideStatusDTO startRide(int N, long riderID);
    RideStatusDTO stopRide(long rideID, String destination, int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins);
    double billRide(long rideID);
}
