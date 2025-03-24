package org.example.services;

import org.example.dto.PaymentDetailsDTO;
import org.example.models.PaymentMethodType;

public interface PaymentService {
    PaymentDetailsDTO processPayment(long rideID, PaymentMethodType paymentMethodType);
    float addMoney(long riderID, float amount);
}
