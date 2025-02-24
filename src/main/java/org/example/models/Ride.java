package org.example.models;

import lombok.Data;

@Data
public class Ride {
    private String riderID;
    private String driverID;
    private int[] destinationCoordinates;
    private int timeTakenInMins;
    private boolean finished;
    private float bill;

    public Ride(String riderID, String driverID) {
        this.riderID = riderID;
        this.driverID = driverID;
        this.finished = false;
    }

    public void finishRide(int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins) {
        this.destinationCoordinates = new int[]{dest_x_coordinate, dest_y_coordinate};
        this.timeTakenInMins = timeTakenInMins;
        this.finished = true;
    }
}
