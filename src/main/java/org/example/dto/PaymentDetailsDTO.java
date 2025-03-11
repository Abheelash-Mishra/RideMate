package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.models.PaymentMethodType;
import org.example.models.PaymentStatus;

@Data
@AllArgsConstructor
public class PaymentDetailsDTO {
    private String paymentID;
    private String senderID;
    private String receiverID;
    private float amount;
    private PaymentMethodType paymentMethodType;
    private PaymentStatus paymentStatus;
}

