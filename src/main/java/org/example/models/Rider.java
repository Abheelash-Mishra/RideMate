package org.example.models;

import lombok.Data;

@Data
public class Rider {
    private int[] coordinates;
    private float walletAmount = 0;

    public Rider(int x_coordinate, int y_coordinate) {
        this.coordinates = new int[]{x_coordinate, y_coordinate};
    }

    public float addMoney(float amount) {
        walletAmount += amount;

        return walletAmount;
    }

    public boolean deductMoney(float amount) {
        if (walletAmount <= amount) {
            return false;
        }

        walletAmount -= amount;
        return true;
    }
}
