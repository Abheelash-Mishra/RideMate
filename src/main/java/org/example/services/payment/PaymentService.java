package org.example.services.payment;

import org.example.database.Database;


public class PaymentService {
    private Payment paymentMethod;

    public PaymentService(PaymentMethodType paymentMethodType, Database db) {
        this.paymentMethod = paymentMethodType.getPaymentMethod(db);
    }

    public void setPaymentMethod(PaymentMethodType paymentMethodType, Database db) {
        this.paymentMethod = paymentMethodType.getPaymentMethod(db);
    }

    public Payment getPaymentMethod() {
        return this.paymentMethod;
    }

    public void processPayment(String rideID) {
        paymentMethod.sendMoney(rideID);
    }
}

