package org.example.services.payment.impl;

import org.example.models.*;
import org.example.repository.Database;

import org.example.services.payment.IPayment;
import org.example.services.payment.PaymentMethodType;

public class WalletPayment implements IPayment {
    private final Database db;

    public WalletPayment(Database db) {
        this.db = db;
    }

    @Override
    public Payment sendMoney(String rideID) {
        Ride currentRide = db.getRideDetails().get(rideID);
        Rider rider = db.getRiderDetails().get(currentRide.getRiderID());

        Driver driver = db.getDriverDetails().get(currentRide.getDriverID());

        boolean success = rider.deductMoney(currentRide.getBill());

        String paymentID = "P-" + rideID;
        if (success) {
            Payment paymentDetails = new Payment(
                    paymentID,
                    currentRide.getRiderID(),
                    currentRide.getDriverID(),
                    currentRide.getBill(),
                    PaymentMethodType.WALLET,
                    PaymentStatus.COMPLETE
            );
            db.getPaymentDetails().put(paymentID, paymentDetails);
            driver.setEarnings(driver.getEarnings() + currentRide.getBill());
        }
        else {
            Payment paymentDetails = new Payment(
                    paymentID,
                    currentRide.getRiderID(),
                    currentRide.getDriverID(),
                    currentRide.getBill(),
                    PaymentMethodType.WALLET,
                    PaymentStatus.FAILED
            );
            db.getPaymentDetails().put(paymentID, paymentDetails);
        }

        return db.getPaymentDetails().get(paymentID);
    }

    public float addMoney(String riderID, float amount) {
        Rider rider = db.getRiderDetails().get(riderID);

        return rider.addMoney(amount);
    }
}
