package models;

public class Rider {
    public String riderID;
    public int[] coordinates;
    private float walletAmount = 0;

    public Rider(String riderID, int x_coordinate, int y_coordinate) {
        this.riderID = riderID;
        this.coordinates = new int[]{x_coordinate, y_coordinate};
    }

    public void addMoney(float amount) {
        walletAmount += amount;

        System.out.println("CURRENT_BALANCE " + riderID + " " + walletAmount);
    }

    public void deductMoney(float amount) {
        if (walletAmount <= amount) {
            System.out.println("LOW_BALANCE");
            return;
        }

        walletAmount -= amount;
        System.out.println("PAID " + amount + " SUCCESSFULLY | CURRENT_BALANCE " + walletAmount);
    }
}
