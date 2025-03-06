package org.example.controllers;

import org.example.models.Payment;
import org.example.services.payment.PaymentMethodType;
import org.example.services.payment.PaymentService;
import org.example.services.payment.impl.WalletPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public Payment pay(
            @RequestParam("rideID") String rideID,
            @RequestParam("type") String paymentMethodType
    ) {
        PaymentMethodType type = PaymentMethodType.valueOf(paymentMethodType.toUpperCase());
        paymentService.setPaymentMethod(type);

        return paymentService.processPayment(rideID);
    }

    @PostMapping("/add-money")
    public ResponseEntity<Float> addMoney(@RequestParam("riderID") String riderID, @RequestParam("amount") float amount) {
        paymentService.setPaymentMethod(PaymentMethodType.WALLET);
        WalletPayment wallet = (WalletPayment) paymentService.getPaymentMethod();

        float balance = wallet.addMoney(riderID, amount);
        return ResponseEntity.ok(balance);
    }
}
