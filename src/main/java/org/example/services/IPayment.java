package org.example.services;

import org.example.dto.PaymentDetailsDTO;

public interface IPayment {
    PaymentDetailsDTO sendMoney(long rideID);
}
