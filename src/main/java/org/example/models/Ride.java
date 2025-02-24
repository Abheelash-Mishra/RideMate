package org.example.models;

public class Ride {
    public String riderID;
    public String driverID;
    public int[] destinationCoordinates;
    public int timeTakenInMins;
    public boolean hasFinished;
    public float bill;

    public Ride(String riderID, String driverID) {
        this.riderID = riderID;
        this.driverID = driverID;
        this.hasFinished = false;
    }

    public void finishRide(int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins) {
        this.destinationCoordinates = new int[]{dest_x_coordinate, dest_y_coordinate};
        this.timeTakenInMins = timeTakenInMins;
        this.hasFinished = true;
    }
}
