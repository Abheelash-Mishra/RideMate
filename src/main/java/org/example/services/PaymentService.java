package org.example.services;

import org.example.dto.PaymentDetailsDTO;
import org.example.models.PaymentMethodType;

public interface PaymentService {
    void setPaymentMethod(PaymentMethodType paymentMethodType);
    PaymentDetailsDTO processPayment(String rideID);
    IPayment getPaymentMethod();
}
