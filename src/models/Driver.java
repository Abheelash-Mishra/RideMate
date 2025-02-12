package models;

public class Driver {
    public final String driverID;
    public final int[] coordinates;
    public boolean available = true;

    public Driver(String driverID, int x_coordinate, int y_coordinate) {
        this.driverID = driverID;
        this.coordinates = new int[]{x_coordinate, y_coordinate};

        System.err.println("Created driver - " + driverID);
    }


    public void updateAvailability() {
        available = !available;
    }
}
