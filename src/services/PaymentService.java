package services;

import database.InMemoryDB;
import models.Driver;
import models.Ride;
import models.Rider;


public class PaymentService {
    private final InMemoryDB db;

    public PaymentService(InMemoryDB db) {
        this.db = db;
    }

    public void payViaWallet(String rideID) {
        Ride currentRide = db.rideDetails.get(rideID);
        Rider rider = db.riderDetails.get(currentRide.riderID);
        Driver driver = db.driverDetails.get(currentRide.driverID);

        rider.deductMoney(currentRide.bill);
        driver.updateEarnings(currentRide.bill);
    }

    public void addMoney(String riderID, float amount) {
        Rider rider = db.riderDetails.get(riderID);

        float walletAmount = rider.addMoney(amount);
        System.out.println("CURRENT_BALANCE " + riderID + " " + walletAmount);
    }
}
