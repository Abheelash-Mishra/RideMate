package services.payment.payment_impl;

import database.InMemoryDB;
import models.Ride;
import services.payment.Payment;

public class UpiPayment implements Payment {
    private final InMemoryDB db;

    public UpiPayment(InMemoryDB db) {
        this.db = db;
    }

    @Override
    public void sendMoney(String rideID) {
        Ride currentRide = db.rideDetails.get(rideID);

        System.out.printf("PAID %s %.1f VIA UPI", currentRide.driverID, currentRide.bill);
    }
}
