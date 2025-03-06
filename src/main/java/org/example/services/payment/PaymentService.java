package org.example.services.payment;

import lombok.Getter;
import org.example.models.Payment;
import org.example.repository.Database;
import org.example.services.payment.impl.WalletPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Service
public class PaymentService {
    private final Database db;
    private IPayment paymentMethod;

    @Autowired
    public PaymentService(Database db) {
        this.db = db;
        this.paymentMethod = new WalletPayment(db);
    }

    public void setPaymentMethod(PaymentMethodType paymentMethodType) {
        this.paymentMethod = paymentMethodType.getPaymentMethod(db);
    }

    public Payment processPayment(String rideID) {
        return paymentMethod.sendMoney(rideID);
    }
}


