package org.example.models;

import lombok.Data;

@Data
public class Rider {
    private final String riderID;
    private int[] coordinates;
    private float walletAmount = 0;

    public Rider(String riderID, int x_coordinate, int y_coordinate) {
        this.riderID = riderID;
        this.coordinates = new int[]{x_coordinate, y_coordinate};
    }
}
