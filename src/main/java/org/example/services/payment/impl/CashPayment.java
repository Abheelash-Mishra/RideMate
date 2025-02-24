package org.example.services.payment.impl;

import org.example.database.Database;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.services.payment.Payment;

public class CashPayment implements Payment {
    private final Database db;

    public CashPayment(Database db) {
        this.db = db;
    }

    @Override
    public void sendMoney(String rideID) {
        Ride currentRide = db.getRideDetails().get(rideID);
        Driver driver = db.getDriverDetails().get(currentRide.driverID);

        driver.updateEarnings(currentRide.bill);
        System.out.printf("PAID %s %.1f VIA CASH\n", currentRide.driverID, currentRide.bill);
    }
}
