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
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CardPayment implements PaymentType {
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public PaymentDetailsDTO sendMoney(long rideID) {
        try {
            Ride currentRide = rideRepository.findById(rideID)
                    .orElseThrow(() -> new InvalidRideException("Invalid Ride ID - " + rideID + ", no such ride exists"));

            Driver driver = currentRide.getDriver();

            Payment paymentDetails = new Payment(
                    currentRide,
                    currentRide.getRider().getRiderID(),
                    driver.getDriverID(),
                    currentRide.getBill(),
                    PaymentMethodType.CARD,
                    PaymentStatus.COMPLETE
            );

            paymentRepository.save(paymentDetails);

            driver.setEarnings(driver.getEarnings() + currentRide.getBill());
            driverRepository.save(driver);

            log.info("Card payment of amount Rs. {} to driver '{}' was successful", currentRide.getBill(), driver.getDriverID());

            return new PaymentDetailsDTO(
                    paymentDetails.getPaymentID(),
                    paymentDetails.getSenderID(),
                    paymentDetails.getReceiverID(),
                    paymentDetails.getAmount(),
                    paymentDetails.getPaymentMethodType(),
                    paymentDetails.getPaymentStatus()
            );
        } catch (Exception e) {
            log.error("Unexpected error while attempting card payment for ride '{}'", rideID);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Payment failed for ride " + rideID, e);
        }
    }
}
