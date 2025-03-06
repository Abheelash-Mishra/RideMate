package org.example.models;

import lombok.Data;
import org.example.services.payment.PaymentMethodType;

@Data
public class Payment {
    private String paymentID;
    private String sender;
    private String receiver;
    private float amount;
    private PaymentMethodType paymentMethodType;
    private PaymentStatus paymentStatus;

    public Payment(String paymentID, String sender, String receiver, float amount, PaymentMethodType paymentMethodType, PaymentStatus paymentStatus) {
        this.paymentID = paymentID;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.paymentMethodType = paymentMethodType;
        this.paymentStatus = paymentStatus;
    }
}
