package org.example.services.payment;

import org.example.models.Payment;

public interface IPayment {
    Payment sendMoney(String rideID);
}
