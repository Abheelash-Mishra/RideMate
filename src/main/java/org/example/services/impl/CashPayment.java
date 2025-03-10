package org.example.services.impl;

import org.example.exceptions.InvalidRideException;
import org.example.models.Payment;
import org.example.models.PaymentStatus;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.repository.DriverRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.RideRepository;
import org.example.services.IPayment;
import org.example.models.PaymentMethodType;
import org.springframework.beans.factory.annotation.Autowired;

public class CashPayment implements IPayment {
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment sendMoney(String rideID) {
        Ride currentRide = rideRepository.findById(rideID)
                .orElseThrow(InvalidRideException::new);

        Driver driver = currentRide.getDriver();

        String paymentID = "P-" + rideID;
        Payment paymentDetails = new Payment(
                paymentID,
                currentRide,
                currentRide.getRider().getRiderID(),
                driver.getDriverID(),
                currentRide.getBill(),
                PaymentMethodType.CASH,
                PaymentStatus.COMPLETE
        );

        paymentRepository.save(paymentDetails);

        driver.setEarnings(driver.getEarnings() + currentRide.getBill());
        driverRepository.save(driver);

        return paymentDetails;
    }
}
