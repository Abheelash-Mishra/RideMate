package services.payment;

import database.InMemoryDB;
import services.payment.payment_impl.Payment;


public class PaymentService {
    private Payment paymentMethod;

    public PaymentService(PaymentMethodType paymentMethodType, InMemoryDB db) {
        this.paymentMethod = paymentMethodType.getPaymentMethod(db);
    }

    public void setPaymentMethod(PaymentMethodType paymentMethodType, InMemoryDB db) {
        this.paymentMethod = paymentMethodType.getPaymentMethod(db);
    }

    public Payment getPaymentMethod() {
        return this.paymentMethod;
    }

    public void processPayment(String rideID) {
        paymentMethod.sendMoney(rideID);
    }
}

