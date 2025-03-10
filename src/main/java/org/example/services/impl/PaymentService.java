package org.example.services.impl;

import lombok.Getter;
import org.example.models.Payment;
import org.example.models.PaymentMethodType;
import org.example.services.IPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Service
public class PaymentService {
    private IPayment paymentMethod;

    @Autowired
    public PaymentService() {
        this.paymentMethod = new WalletPayment();
    }

    public void setPaymentMethod(PaymentMethodType paymentMethodType) {
        this.paymentMethod = paymentMethodType.getPaymentMethod();
    }

    public Payment processPayment(String rideID) {
        return paymentMethod.sendMoney(rideID);
    }
}


