package org.example.services;

import org.example.dto.PaymentDetailsDTO;
import org.example.dto.TransactionDetailsDTO;
import org.example.models.PaymentMethodType;

import java.util.List;

public interface PaymentService {
    PaymentDetailsDTO processPayment(long rideID, PaymentMethodType paymentMethodType);
    float addMoney(float amount, PaymentMethodType rechargeMethodType);
    float getBalance();
    List<TransactionDetailsDTO> getAllTransactions();
    long getUserId();
}
