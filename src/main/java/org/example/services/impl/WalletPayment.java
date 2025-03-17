package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.PaymentDetailsDTO;
import org.example.exceptions.InvalidDriverIDException;
import org.example.exceptions.InvalidRideException;
import org.example.exceptions.InvalidRiderIDException;
import org.example.models.*;

import org.example.repository.DriverRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.RideRepository;
import org.example.repository.RiderRepository;
import org.example.services.IPayment;
import org.example.models.PaymentMethodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Slf4j
@Component
public class WalletPayment implements IPayment {
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public PaymentDetailsDTO sendMoney(long rideID) {
        try {
            Ride currentRide = rideRepository.findById(rideID)
                    .orElseThrow(InvalidRideException::new);

            long riderID = currentRide.getRider().getRiderID();
            Rider rider = riderRepository.findById(riderID)
                    .orElseThrow(InvalidRiderIDException::new);

            long driverID = currentRide.getDriver().getDriverID();
            Driver driver = driverRepository.findById(driverID)
                    .orElseThrow(() -> new InvalidDriverIDException(driverID, new NoSuchElementException("Driver not present in database")));

            boolean success;
            if (rider.getWalletAmount() <= currentRide.getBill()) {
                log.info("Rider '{}' had insufficient balance in their wallet", riderID);
                success = false;
            }
            else {
                rider.setWalletAmount(rider.getWalletAmount() - currentRide.getBill());
                success = true;
            }

            Payment paymentDetails;
            if (success) {
                log.info("Wallet payment of amount Rs. {} to driver '{}' was successful", currentRide.getBill(), driver.getDriverID());

                paymentDetails = new Payment(
                        currentRide,
                        currentRide.getRider().getRiderID(),
                        driver.getDriverID(),
                        currentRide.getBill(),
                        PaymentMethodType.WALLET,
                        PaymentStatus.COMPLETE
                );
                driver.setEarnings(driver.getEarnings() + currentRide.getBill());
            }
            else {
                log.info("Wallet payment of amount Rs. {} to driver '{}' had failed", currentRide.getBill(), driver.getDriverID());

                paymentDetails = new Payment(
                        currentRide,
                        currentRide.getRider().getRiderID(),
                        driver.getDriverID(),
                        currentRide.getBill(),
                        PaymentMethodType.WALLET,
                        PaymentStatus.FAILED
                );
            }

            paymentRepository.save(paymentDetails);
            riderRepository.save(rider);
            driverRepository.save(driver);

            return new PaymentDetailsDTO(
                    paymentDetails.getPaymentID(),
                    paymentDetails.getSenderID(),
                    paymentDetails.getReceiverID(),
                    paymentDetails.getAmount(),
                    paymentDetails.getPaymentMethodType(),
                    paymentDetails.getPaymentStatus()
            );
        } catch (Exception e) {
            log.error("Unexpected error while attempting wallet payment for ride '{}' | Error: {}", rideID, e.getMessage(), e);
            throw new RuntimeException("Payment failed for ride " + rideID, e);
        }
    }

    public float addMoney(long riderID, float amount) {
        try {
            Rider rider = riderRepository.findById(riderID)
                    .orElseThrow(InvalidRiderIDException::new);

            rider.setWalletAmount(rider.getWalletAmount() + amount);
            riderRepository.save(rider);

            log.info("Successfully added Rs. {} to wallet of rider '{}'", amount, riderID);

            return rider.getWalletAmount();
        } catch (InvalidRiderIDException e) {
            log.error("Unexpected error while adding funds to wallet of rider '{}' | Error: {}", riderID, e.getMessage(), e);
            throw new RuntimeException("Wallet recharge failed for rider " + riderID, e);
        }
    }
}
