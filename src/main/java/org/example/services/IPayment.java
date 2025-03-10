package org.example.services;

import org.example.models.Payment;

public interface IPayment {
    Payment sendMoney(String rideID);
}
