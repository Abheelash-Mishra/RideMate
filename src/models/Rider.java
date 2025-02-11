package models;

public class Rider {
    public String riderID;
    public int[] coordinates;

    public Rider(String riderID, int x_coordinate, int y_coordinate) {
        this.riderID = riderID;
        this.coordinates = new int[]{x_coordinate, y_coordinate};

        System.out.println("Created rider - " + riderID);
    }
}
