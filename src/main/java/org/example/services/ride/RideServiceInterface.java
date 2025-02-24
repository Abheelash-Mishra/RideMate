package org.example.services.ride;

public interface RideServiceInterface {
    void addRider(String riderID, int x_coordinate, int y_coordinate);
    void matchRider(String riderID);
    void startRide(String rideID, int N, String riderID);
    void stopRide(String rideID, int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins);
    void billRide(String rideID);
}
