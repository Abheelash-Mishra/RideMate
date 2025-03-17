package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.PaymentDetailsDTO;
import org.example.models.PaymentMethodType;
import org.example.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
            log.error("Could not complete payment unexpectedly | Exception: {}", e.getMessage(), e);

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-money")
    public ResponseEntity<Float> addMoney(@RequestParam("riderID") long riderID, @RequestParam("amount") float amount) {
        log.info("Accessing endpoint: /payment/add-money | riderID={}, amount={}", riderID, amount);

        try {
            float balance = paymentService.addMoney(riderID, amount);

            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            log.error("Could not add money to the rider's wallet unexpectedly | Exception: {}", e.getMessage(), e);

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
