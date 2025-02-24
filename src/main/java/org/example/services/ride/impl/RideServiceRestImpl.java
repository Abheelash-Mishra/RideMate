package org.example.services.ride.impl;

import org.example.database.Database;
import org.example.services.ride.RideServiceInterface;

public class RideServiceRestImpl implements RideServiceInterface {
    private final Database db;

    public RideServiceRestImpl(Database db) {
        this.db = db;
    }


    @Override
    public void addRider(String riderID, int x_coordinate, int y_coordinate) {
        System.out.println("ADDING RIDER");
    }

    @Override
    public void matchRider(String riderID) {
        System.out.println("MATCHING RIDER");
    }

    @Override
    public void startRide(String rideID, int N, String riderID) {
        System.out.println("STARTING RIDE");
    }

    @Override
    public void stopRide(String rideID, int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins) {
        System.out.println("STOPPING RIDE");
    }

    @Override
    public void billRide(String rideID) {
        System.out.println("BILLING RIDER");
    }
}
