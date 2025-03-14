package org.example.services;

import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideStatusDTO;

public interface RideService {
    void addRider(long riderID, int x_coordinate, int y_coordinate);
    MatchedDriversDTO matchRider(long riderID);
    RideStatusDTO startRide(long rideID, int N, long riderID);
    RideStatusDTO stopRide(long rideID, int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins);
    double billRide(long rideID);
}
