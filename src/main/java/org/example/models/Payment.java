package org.example.models;

import lombok.Data;
import org.example.services.payment.PaymentMethodType;

@Data
public class Payment {
    private String paymentID;
    private String senderID;
    private String receiverID;
    private float amount;
    private PaymentMethodType paymentMethodType;
    private PaymentStatus paymentStatus;

    public Payment(String paymentID, String senderID, String receiverID, float amount, PaymentMethodType paymentMethodType, PaymentStatus paymentStatus) {
        this.paymentID = paymentID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.amount = amount;
        this.paymentMethodType = paymentMethodType;
        this.paymentStatus = paymentStatus;
    }
}
