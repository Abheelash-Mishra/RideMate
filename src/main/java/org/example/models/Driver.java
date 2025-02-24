package org.example.models;

import lombok.Data;

@Data
public class Driver {
    private final int[] coordinates;
    private boolean available = true;
    private float earnings = 0;
    private float rating = 0;
    private float ratingSum = 0;
    private int ridesDone = 0;

    public Driver(int x_coordinate, int y_coordinate) {
        this.coordinates = new int[]{x_coordinate, y_coordinate};
    }

    public float updateDriverRating(float newRate) {
        ridesDone++;
        ratingSum += newRate;
        this.rating = ratingSum / ridesDone;

        return this.rating;
    }

    public void updateEarnings(float amount) {
        earnings += amount;
    }

    public void updateAvailability() {
        available = !available;
    }
}
