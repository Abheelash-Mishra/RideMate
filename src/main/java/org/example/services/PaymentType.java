package org.example.services;

import org.example.dto.PaymentDetailsDTO;

public interface PaymentType {
    PaymentDetailsDTO sendMoney(long rideID);
}
