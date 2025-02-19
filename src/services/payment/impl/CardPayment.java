package services.payment.impl;

import database.Database;
import models.Driver;
import models.Ride;
import services.payment.Payment;

public class CardPayment implements Payment {
    private final Database db;

    public CardPayment(Database db) {
        this.db = db;
    }

    @Override
    public void sendMoney(String rideID) {
        Ride currentRide = db.getRideDetails().get(rideID);
        Driver driver = db.getDriverDetails().get(currentRide.driverID);

        driver.updateEarnings(currentRide.bill);
        System.out.printf("PAID %s %.1f VIA CARD\n", currentRide.driverID, currentRide.bill);
    }
}
