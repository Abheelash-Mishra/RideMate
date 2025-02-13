package models;

public class Driver {
    public final String driverID;
    public final int[] coordinates;
    public boolean available = true;
    public float rating = 0;
    private float ratingSum = 0;
    private int ridesDone = 0;

    public Driver(String driverID, int x_coordinate, int y_coordinate) {
        this.driverID = driverID;
        this.coordinates = new int[]{x_coordinate, y_coordinate};
    }

    public float updateDriverRating(float newRate) {
        ridesDone++;
        ratingSum += newRate;

        this.rating = ratingSum / ridesDone;

        return this.rating;
    }

    public void updateAvailability() {
        available = !available;
    }
}
