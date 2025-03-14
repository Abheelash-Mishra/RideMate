package org.example.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long paymentID;

    @OneToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    private long senderID;
    private long receiverID;
    private float amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public Payment(Ride ride, long senderID, long receiverID, float amount, PaymentMethodType paymentMethodType, PaymentStatus paymentStatus) {
        this.ride = ride;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.amount = amount;
        this.paymentMethodType = paymentMethodType;
        this.paymentStatus = paymentStatus;
    }
}

