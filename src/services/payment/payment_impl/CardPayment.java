package services.payment.payment_impl;

import database.InMemoryDB;
import models.Driver;
import models.Ride;
import services.payment.Payment;

public class CardPayment implements Payment {
    private final InMemoryDB db;

    public CardPayment(InMemoryDB db) {
        this.db = db;
    }

    @Override
    public void sendMoney(String rideID) {
        Ride currentRide = db.rideDetails.get(rideID);
        Driver driver = db.driverDetails.get(currentRide.driverID);

        driver.updateEarnings(currentRide.bill);
        System.out.printf("PAID %s %.1f VIA CARD\n", currentRide.driverID, currentRide.bill);
    }
}
