package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.PaymentDetailsDTO;
import org.example.dto.TransactionDetailsDTO;
import org.example.models.PaymentMethodType;
import org.example.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
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
        log.info("Accessing endpoint: /payment/pay | rideID={}, type={}", rideID, paymentMethodType);

        try {
            PaymentMethodType type = PaymentMethodType.valueOf(paymentMethodType.toUpperCase());

            return ResponseEntity.ok(paymentService.processPayment(rideID, type));
        } catch (Exception e) {
            log.error("Could not complete payment unexpectedly for ride '{}' via {}", rideID, paymentMethodType);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Payment failed unexpectedly, please try again later", e);
        }
    }

    @PostMapping("/add-money")
    public ResponseEntity<Float> addMoney(
            @RequestParam("riderID") long riderID,
            @RequestParam("amount") float amount,
            @RequestParam("type") String paymentMethodType
    ) {
        log.info("Accessing endpoint: /payment/add-money | riderID={}, amount={}, type={}", riderID, amount, paymentMethodType);

        try {
            PaymentMethodType type = PaymentMethodType.valueOf(paymentMethodType.toUpperCase());
            float balance = paymentService.addMoney(riderID, amount, type);

            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            log.error("Could not add funds to the rider {}'s wallet unexpectedly", riderID);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Wallet recharge failed unexpectedly, please try again later", e);
        }
    }

    @GetMapping("/wallet")
    public ResponseEntity<Float> fetchBalance(@RequestParam("riderID") long riderID) {
        log.info("Accessing endpoint: /payment/wallet | riderID={}", riderID);
        float balance = paymentService.getBalance(riderID);

        return ResponseEntity.ok(balance);
    }

    @GetMapping("/wallet/transactions")
    public ResponseEntity<List<TransactionDetailsDTO>> fetchAllTransactions(@RequestParam("riderID") long riderID) {
        log.info("Accessing endpoint: /payment/wallet/transactions || riderID={}", riderID);

        return ResponseEntity.ok(paymentService.getAllTransactions(riderID));
    }
}
