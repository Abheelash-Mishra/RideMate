package org.example.controllers;

import org.example.dto.PaymentDetailsDTO;
import org.example.models.PaymentMethodType;
import org.example.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentDetailsDTO> pay(
            @RequestParam("rideID") long rideID,
            @RequestParam("type") String paymentMethodType
    ) {
        PaymentMethodType type = PaymentMethodType.valueOf(paymentMethodType.toUpperCase());

        return ResponseEntity.ok(paymentService.processPayment(rideID, type));
    }

    @PostMapping("/add-money")
    public ResponseEntity<Float> addMoney(@RequestParam("riderID") long riderID, @RequestParam("amount") float amount) {
        float balance = paymentService.addMoney(riderID, amount);

        return ResponseEntity.ok(balance);
    }
}
