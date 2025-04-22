package org.example.unit;

import org.example.models.*;
import org.example.dto.PaymentDetailsDTO;
import org.example.repository.*;
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

    @MockitoBean
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private PaymentService paymentService;

    private Rider testRider;

    @BeforeEach
    void setUp() {
        long rideID = 1;

        long riderID = 1;
        testRider = new Rider("r1@email.com", "9876556789", 0, 0);
        testRider.setRiderID(riderID);
        testRider.setMatchedDrivers(List.of(1L, 3L));

        long driverID = 3;
        Driver testDriver = new Driver("d1@email.com", "9876556789", 2, 2);
        testDriver.setDriverID(driverID);

        Ride testRide = new Ride(testRider, testDriver);
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
        PaymentDetailsDTO expected = new PaymentDetailsDTO(
                0,
                1,
                3,
                201.3F,
                PaymentMethodType.CARD,
                PaymentStatus.COMPLETE
        );

        PaymentDetailsDTO response = paymentService.processPayment(1, PaymentMethodType.CARD);

        assertEquals(expected, response, "Payment was not executed as expected");
    }

    @Test
    void processUPIPayment() {
        PaymentDetailsDTO expected = new PaymentDetailsDTO(
                0,
                1,
                3,
                201.3F,
                PaymentMethodType.UPI,
                PaymentStatus.COMPLETE
        );

        PaymentDetailsDTO response = paymentService.processPayment(1, PaymentMethodType.UPI);

        assertEquals(expected, response, "Payment was not executed as expected");
    }

    @Test
    void processCashPayment() {
        PaymentDetailsDTO expected = new PaymentDetailsDTO(
                0,
                1,
                3,
                201.3F,
                PaymentMethodType.CASH,
                PaymentStatus.COMPLETE
        );

        PaymentDetailsDTO response = paymentService.processPayment(1, PaymentMethodType.CASH);

        assertEquals(expected, response, "Payment was not executed as expected");
    }

    @Test
    void processWalletPayment() {
        testRider.setWalletAmount(500F);

        PaymentDetailsDTO expected = new PaymentDetailsDTO(
                0,
                1,
                3,
                201.3F,
                PaymentMethodType.WALLET,
                PaymentStatus.COMPLETE
        );

        WalletTransaction walletTransaction = new WalletTransaction(testRider, -201.3F, null);
        when(walletTransactionRepository.save(any(WalletTransaction.class))).thenReturn(walletTransaction);

        PaymentDetailsDTO response = paymentService.processPayment(1, PaymentMethodType.WALLET);

        assertEquals(expected, response, "Payment was not executed as expected");
        assertEquals(298.7F, testRider.getWalletAmount(), 0.1, "Wallet was not deducted");
    }
}