package org.example.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Payment {
    @Id
    private String paymentID;

    @OneToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    private String senderID;
    private String receiverID;
    private float amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public Payment(String paymentID, Ride ride, String senderID, String receiverID, float amount, PaymentMethodType paymentMethodType, PaymentStatus paymentStatus) {
        this.paymentID = paymentID;
        this.ride = ride;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.amount = amount;
        this.paymentMethodType = paymentMethodType;
        this.paymentStatus = paymentStatus;
    }
}

