package org.example.services.ride;

import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideStatusDTO;

public interface RideService {
    void addRider(String riderID, int x_coordinate, int y_coordinate);
    MatchedDriversDTO matchRider(String riderID);
    RideStatusDTO startRide(String rideID, int N, String riderID);
    RideStatusDTO stopRide(String rideID, int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins);
    double billRide(String rideID);
}
