package org.example.models;

import lombok.Data;

@Data
public class Driver {
    private final String driverID;
    private final int[] coordinates;
    private boolean available = true;
    private float earnings = 0;
    private float rating = 0;
    private float ratingSum = 0;
    private int ridesDone = 0;

    public Driver(String driverID, int x_coordinate, int y_coordinate) {
        this.driverID = driverID;
        this.coordinates = new int[]{x_coordinate, y_coordinate};
    }
}
