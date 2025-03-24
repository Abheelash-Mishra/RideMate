package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.models.PaymentMethodType;
import org.example.models.PaymentStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsDTO {
    private long paymentID;
    private long senderID;
    private long receiverID;
    private float amount;
    private PaymentMethodType paymentMethodType;
    private PaymentStatus paymentStatus;
}

