package org.example.services.payment.impl;

import org.example.repository.Database;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.services.payment.Payment;

public class CashPayment implements Payment {
    private final Database db;

    public CashPayment(Database db) {
        this.db = db;
    }

    @Override
    public String sendMoney(String rideID) {
        Ride currentRide = db.getRideDetails().get(rideID);
        Driver driver = db.getDriverDetails().get(currentRide.getDriverID());

        driver.updateEarnings(currentRide.getBill());
        return String.format("PAID %s %.1f VIA CASH", currentRide.getDriverID(), currentRide.getBill());
    }
}
