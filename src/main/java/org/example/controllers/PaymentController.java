package org.example.controllers;

import org.example.services.payment.PaymentMethodType;
import org.example.services.payment.PaymentService;
import org.example.services.payment.impl.WalletPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public String pay(
            @RequestParam("rideID") String rideID,
            @RequestParam("type") String paymentMethodType
    ) {
        PaymentMethodType type = PaymentMethodType.valueOf(paymentMethodType.toUpperCase());
        paymentService.setPaymentMethod(type);

        return paymentService.processPayment(rideID);
    }

    @PostMapping("/add-money")
    public String addMoney(@RequestParam("riderID") String riderID, @RequestParam("amount") float amount) {
        paymentService.setPaymentMethod(PaymentMethodType.WALLET);
        WalletPayment wallet = (WalletPayment) paymentService.getPaymentMethod();

        float balance = wallet.addMoney(riderID, amount);
        return "CURRENT_BALANCE " + riderID + " " + balance;
    }
}
