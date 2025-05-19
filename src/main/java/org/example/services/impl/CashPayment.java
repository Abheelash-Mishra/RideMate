package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.PaymentDetailsDTO;
import org.example.exceptions.InvalidRideException;
import org.example.models.Payment;
import org.example.models.PaymentStatus;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.repository.DriverRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.RideRepository;
import org.example.services.PaymentType;
import org.example.models.PaymentMethodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Slf4j
@Component
public class CashPayment implements PaymentType {
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public PaymentDetailsDTO sendMoney(long rideID) {
        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(() -> new InvalidRideException("Invalid Ride ID - " + rideID + ", no such ride exists"));

        try {
            Driver driver = currentRide.getDriver();
            long riderID = currentRide.getRider().getId();

            Payment paymentDetails = new Payment(
                    currentRide,
                    riderID,
                    driver.getId(),
                    currentRide.getBill(),
                    PaymentMethodType.CASH,
                    PaymentStatus.COMPLETE
            );

            paymentRepository.save(paymentDetails);

            driver.setEarnings(driver.getEarnings() + currentRide.getBill());
            driverRepository.save(driver);

            Objects.requireNonNull(cacheManager.getCache("walletAmount")).evict(riderID);
            Objects.requireNonNull(cacheManager.getCache("allTransactions")).evict(riderID);

            log.info("Cash payment of amount Rs. {} to driver '{}' was successful", currentRide.getBill(), driver.getId());

            return new PaymentDetailsDTO(
                    paymentDetails.getPaymentID(),
                    paymentDetails.getSenderID(),
                    paymentDetails.getReceiverID(),
                    paymentDetails.getAmount(),
                    paymentDetails.getPaymentMethodType(),
                    paymentDetails.getPaymentStatus()
            );
        } catch (Exception e) {
            log.error("Unexpected error while attempting cash payment for ride '{}'", rideID);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Payment failed for ride " + rideID, e);
        }
    }
}
