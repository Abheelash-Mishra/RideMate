package org.example.services.payment.impl;

import org.example.database.Database;

import org.example.models.Driver;
import org.example.models.Ride;
import org.example.models.Rider;
import org.example.services.payment.Payment;

public class WalletPayment implements Payment {
    private final Database db;

    public WalletPayment(Database db) {
        this.db = db;
    }

    @Override
    public void sendMoney(String rideID) {
        Ride currentRide = db.getRideDetails().get(rideID);
        Rider rider = db.getRiderDetails().get(currentRide.riderID);

        Driver driver = db.getDriverDetails().get(currentRide.driverID);

        rider.deductMoney(currentRide.bill);
        driver.updateEarnings(currentRide.bill);
    }

    public void addMoney(String riderID, float amount) {
        Rider rider = db.getRiderDetails().get(riderID);

        float walletAmount = rider.addMoney(amount);
        System.out.println("CURRENT_BALANCE " + riderID + " " + walletAmount);
    }
}
