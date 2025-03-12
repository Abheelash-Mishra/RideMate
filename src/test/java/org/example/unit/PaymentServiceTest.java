package org.example.unit;

import org.example.models.*;
import org.example.dto.PaymentDetailsDTO;
import org.example.repository.DriverRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.RideRepository;
import org.example.repository.RiderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.services.PaymentService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @MockitoBean
    private RideRepository rideRepository;

    @MockitoBean
    private RiderRepository riderRepository;

    @MockitoBean
    private DriverRepository driverRepository;

    @MockitoBean
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    private Ride testRide;
    private Rider testRider;
    private Driver testDriver;

    @BeforeEach
    void setUp() {
        String rideID = "RIDE-001";

        String riderID = "R1";
        testRider = new Rider(riderID, 0, 0);
        testRider.setMatchedDrivers(List.of("D1", "D3"));

        String driverID = "D3";
        testDriver = new Driver(driverID, 2, 2);

        testRide = new Ride(rideID, testRider, testDriver);
        testRide.setDestinationCoordinates(List.of(4, 5));
        testRide.setTimeTakenInMins(32);
        testRide.setStatus(RideStatus.FINISHED);
        testRide.setBill(201.3F);

        when(rideRepository.findById(rideID)).thenReturn(Optional.of(testRide));
        when(riderRepository.findById(riderID)).thenReturn(Optional.of(testRider));
        when(driverRepository.findById(driverID)).thenReturn(Optional.of(testDriver));

        when(paymentRepository.save(any(Payment.class))).thenReturn(null);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);
    }

    @Test
    void processCardPayment() {
        paymentService.setPaymentMethod(PaymentMethodType.CARD);

        PaymentDetailsDTO expected = new PaymentDetailsDTO(
                "P-RIDE-001",
                "R1",
                "D3",
                201.3F,
                PaymentMethodType.CARD,
                PaymentStatus.COMPLETE
        );

        PaymentDetailsDTO response = paymentService.processPayment("RIDE-001");

        assertEquals(expected, response, "Payment was not executed as expected");
    }

    @Test
    void processUPIPayment() {
        paymentService.setPaymentMethod(PaymentMethodType.UPI);

        PaymentDetailsDTO expected = new PaymentDetailsDTO(
                "P-RIDE-001",
                "R1",
                "D3",
                201.3F,
                PaymentMethodType.UPI,
                PaymentStatus.COMPLETE
        );

        PaymentDetailsDTO response = paymentService.processPayment("RIDE-001");

        assertEquals(expected, response, "Payment was not executed as expected");
    }

    @Test
    void processCashPayment() {
        paymentService.setPaymentMethod(PaymentMethodType.CASH);

        PaymentDetailsDTO expected = new PaymentDetailsDTO(
                "P-RIDE-001",
                "R1",
                "D3",
                201.3F,
                PaymentMethodType.CASH,
                PaymentStatus.COMPLETE
        );

        PaymentDetailsDTO response = paymentService.processPayment("RIDE-001");

        assertEquals(expected, response, "Payment was not executed as expected");
    }

    @Test
    void processWalletPayment() {
        paymentService.setPaymentMethod(PaymentMethodType.WALLET);

        testRider.setWalletAmount(500F);

        PaymentDetailsDTO expected = new PaymentDetailsDTO(
                "P-RIDE-001",
                "R1",
                "D3",
                201.3F,
                PaymentMethodType.WALLET,
                PaymentStatus.COMPLETE
        );

        PaymentDetailsDTO response = paymentService.processPayment("RIDE-001");

        assertEquals(expected, response, "Payment was not executed as expected");
        assertEquals(298.7F, testRider.getWalletAmount(), 0.1, "Wallet was not deducted");
    }
}