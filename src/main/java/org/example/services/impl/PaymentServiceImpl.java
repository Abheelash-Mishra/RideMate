package org.example.services.impl;

import org.example.dto.PaymentDetailsDTO;
import org.example.exceptions.InvalidRiderIDException;
import org.example.models.PaymentMethodType;
import org.example.models.Rider;
import org.example.repository.RiderRepository;
import org.example.services.PaymentType;
import org.example.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final HashMap<PaymentMethodType, PaymentType> paymentMethods;

    private PaymentType paymentMethod;

    @Autowired
    private RiderRepository riderRepository;

    public PaymentServiceImpl(List<PaymentType> paymentImplementations) {
        this.paymentMethods = new HashMap<>();

        for (PaymentType payment : paymentImplementations) {
            PaymentMethodType type = getPaymentType(payment);
            this.paymentMethods.put(type, payment);
        }

        // Default payment method
        this.paymentMethod = this.paymentMethods.get(PaymentMethodType.WALLET);
    }

    @Override
    public PaymentDetailsDTO processPayment(long rideID, PaymentMethodType paymentMethodType) {
        this.paymentMethod = this.paymentMethods.get(paymentMethodType);

        return paymentMethod.sendMoney(rideID);
    }

    @Override
    public float addMoney(long riderID, float amount) {
        this.paymentMethod = this.paymentMethods.get(PaymentMethodType.WALLET);

        WalletPayment walletPayment = (WalletPayment) this.paymentMethod;

        return walletPayment.addMoney(riderID, amount);
    }

    @Override
    public float getBalance(long riderID) {
        Rider rider = riderRepository.findById(riderID)
                .orElseThrow(() -> new InvalidRiderIDException("Invalid Rider ID - " + riderID + " || No such rider exists"));

        return rider.getWalletAmount();
    }

    private PaymentMethodType getPaymentType(PaymentType payment) {
        if (payment instanceof WalletPayment) return PaymentMethodType.WALLET;
        if (payment instanceof CashPayment) return PaymentMethodType.CASH;
        if (payment instanceof CardPayment) return PaymentMethodType.CARD;
        if (payment instanceof UpiPayment) return PaymentMethodType.UPI;

        throw new IllegalArgumentException("Unknown Payment Type");
    }
}



