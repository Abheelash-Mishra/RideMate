package org.example.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long transactionID;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    private float amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType rechargeMethodType;

    public WalletTransaction(Rider rider, float amount, PaymentMethodType rechargeMethodType) {
        this.rider = rider;
        this.amount = amount;
        this.rechargeMethodType = rechargeMethodType;
    }
}
