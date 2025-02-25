package org.example.services.ride;

public interface RideService {
    void addRider(String riderID, int x_coordinate, int y_coordinate);
    String matchRider(String riderID);
    String startRide(String rideID, int N, String riderID);
    String stopRide(String rideID, int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins);
    double billRide(String rideID);
}
