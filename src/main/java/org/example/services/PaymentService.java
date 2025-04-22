package org.example.services;

import org.example.dto.PaymentDetailsDTO;
import org.example.dto.TransactionDetailsDTO;
import org.example.models.PaymentMethodType;

import java.util.List;

public interface PaymentService {
    PaymentDetailsDTO processPayment(long rideID, PaymentMethodType paymentMethodType);
    float addMoney(long riderID, float amount, PaymentMethodType rechargeMethodType);
    float getBalance(long riderID);
    List<TransactionDetailsDTO> getAllTransactions(long riderID);
}
