package org.example.services.ride;

import org.example.services.ride.exceptions.InvalidRideException;

public class RideService {
    private final RideServiceInterface rideServiceImpl;

    public RideService(RideServiceInterface rideServiceImpl) {
        this.rideServiceImpl = rideServiceImpl;
    }


    public void addRider(String riderID, int x_coordinate, int y_coordinate) {
        rideServiceImpl.addRider(riderID, x_coordinate, y_coordinate);
    }

    /**
     * Available drivers need to be matched with the rider that are 5km or closer.
     * If they are equidistant, the drivers are sorted lexicographically.
     */
    public void matchRider(String riderID) {
        rideServiceImpl.matchRider(riderID);
    }


    public void startRide(String rideID, int N, String riderID) throws InvalidRideException {
        rideServiceImpl.startRide(rideID, N, riderID);
    }

    public void stopRide(String rideID, int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins) throws InvalidRideException {
        rideServiceImpl.stopRide(rideID, dest_x_coordinate, dest_y_coordinate, timeTakenInMins);
    }

    public void billRide(String rideID) {
        rideServiceImpl.billRide(rideID);
    }
}
