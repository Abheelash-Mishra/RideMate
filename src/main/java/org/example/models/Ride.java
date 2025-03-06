package org.example.models;

import lombok.Data;

@Data
public class Ride {
    private String riderID;
    private String driverID;
    private int[] destinationCoordinates;
    private RideStatus status;
    private int timeTakenInMins;
    private float bill;

    public Ride(String riderID, String driverID) {
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
