package org.example.services.payment.impl;

import org.example.models.Payment;
import org.example.models.PaymentStatus;
import org.example.repository.Database;

import org.example.models.Driver;
import org.example.models.Ride;
import org.example.services.payment.IPayment;
import org.example.services.payment.PaymentMethodType;

public class UpiPayment implements IPayment {
    private final Database db;

    public UpiPayment(Database db) {
        this.db = db;
    }

    @Override
    public Payment sendMoney(String rideID) {
        Ride currentRide = db.getRideDetails().get(rideID);
        Driver driver = db.getDriverDetails().get(currentRide.getDriverID());

        String paymentID = "P-" + rideID;
        Payment paymentDetails = new Payment(
                paymentID,
                currentRide.getRiderID(),
                currentRide.getDriverID(),
                currentRide.getBill(),
                PaymentMethodType.UPI,
                PaymentStatus.COMPLETE
        );
        db.getPaymentDetails().put(paymentID, paymentDetails);
        driver.setEarnings(driver.getEarnings() + currentRide.getBill());

        return db.getPaymentDetails().get(paymentID);
    }
}
