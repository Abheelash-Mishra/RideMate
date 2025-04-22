package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.models.PaymentMethodType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailsDTO {
    private long transactionID;
    private float amount;
    private PaymentMethodType rechargeMethodType;
}
