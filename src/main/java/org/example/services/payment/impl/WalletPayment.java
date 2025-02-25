package org.example.services.payment.impl;

import org.example.repository.Database;

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
    public String sendMoney(String rideID) {
        Ride currentRide = db.getRideDetails().get(rideID);
        Rider rider = db.getRiderDetails().get(currentRide.getRiderID());

        Driver driver = db.getDriverDetails().get(currentRide.getDriverID());

        boolean success = rider.deductMoney(currentRide.getBill());

        if (success){
            driver.updateEarnings(currentRide.getBill());
            return "PAID " + currentRide.getBill() + " SUCCESSFULLY | CURRENT_BALANCE " + rider.getWalletAmount();
        }

        return "LOW_BALANCE";
    }

    public float addMoney(String riderID, float amount) {
        Rider rider = db.getRiderDetails().get(riderID);

        return rider.addMoney(amount);
    }
}
