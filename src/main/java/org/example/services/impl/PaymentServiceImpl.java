package org.example.services.impl;

import lombok.Getter;
import org.example.dto.PaymentDetailsDTO;
import org.example.models.PaymentMethodType;
import org.example.services.IPayment;
import org.example.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final HashMap<PaymentMethodType, IPayment> paymentMethods;

    @Getter
    private IPayment paymentMethod;

    @Autowired
    public PaymentServiceImpl(List<IPayment> paymentImplementations) {
        this.paymentMethods = new HashMap<>();

        for (IPayment payment : paymentImplementations) {
            PaymentMethodType type = getPaymentType(payment);
            this.paymentMethods.put(type, payment);
        }

        // Default payment method
        this.paymentMethod = this.paymentMethods.get(PaymentMethodType.WALLET);
    }

    public void setPaymentMethod(PaymentMethodType paymentMethodType) {
        this.paymentMethod = paymentMethods.get(paymentMethodType);
    }

    public PaymentDetailsDTO processPayment(String rideID) {
        return paymentMethod.sendMoney(rideID);
    }

    private PaymentMethodType getPaymentType(IPayment payment) {
        if (payment instanceof WalletPayment) return PaymentMethodType.WALLET;
        if (payment instanceof CashPayment) return PaymentMethodType.CASH;
        if (payment instanceof CardPayment) return PaymentMethodType.CARD;
        if (payment instanceof UpiPayment) return PaymentMethodType.UPI;

        throw new IllegalArgumentException("Unknown Payment Type");
    }
}



