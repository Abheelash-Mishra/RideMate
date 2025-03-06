package org.example.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Ride {
    private final String rideID;
    private final String riderID;
    private final String driverID;
    private int[] destinationCoordinates;
    private RideStatus status;
    private int timeTakenInMins;
    @Setter private float bill;

    public Ride(String rideID, String riderID, String driverID) {
        this.rideID = rideID;
        this.riderID = riderID;
        this.driverID = driverID;
        this.status = RideStatus.STARTED;
    }

    public void finishRide(int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins) {
        this.destinationCoordinates = new int[]{dest_x_coordinate, dest_y_coordinate};
        this.timeTakenInMins = timeTakenInMins;
        this.status = RideStatus.FINISHED;
    }
}
