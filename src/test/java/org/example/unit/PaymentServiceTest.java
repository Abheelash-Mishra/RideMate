package org.example.unit;

import org.example.config.TestConfig;
import org.example.models.*;
import org.example.repository.Database;

import org.example.services.payment.PaymentMethodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.services.payment.PaymentService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class PaymentServiceTest {
    @Autowired
    private Database mockDB;

    @Autowired
    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        HashMap<String, Ride> rides = new HashMap<>();
        HashMap<String, Driver> drivers = new HashMap<>();
        HashMap<String, Rider> riders = new HashMap<>();
        HashMap<String, Payment> payments = new HashMap<>();

        when(mockDB.getRideDetails()).thenReturn(rides);
        when(mockDB.getDriverDetails()).thenReturn(drivers);
        when(mockDB.getRiderDetails()).thenReturn(riders);
        when(mockDB.getPaymentDetails()).thenReturn(payments);

        drivers.put("D3", new Driver("D3", 2, 2));

        Ride ride = new Ride("RIDE-001", "R1", "D3");
        ride.finishRide(5, 5, 20);
        ride.setBill(201.3F);
        rides.put("RIDE-001", ride);
    }

    @Test
    void processCardPayment() {
        paymentService.setPaymentMethod(PaymentMethodType.CARD);

        Payment result = paymentService.processPayment("RIDE-001");

        assertNotNull(result);
        assertEquals("P-RIDE-001", result.getPaymentID(), "Payment ID should be correctly generated");
        assertEquals("R1", result.getSenderID(), "Rider ID should match");
        assertEquals("D3", result.getReceiverID(), "Driver ID should match");
        assertEquals(201.3F, result.getAmount(), 0.01, "Payment amount should match the ride bill");
        assertEquals(PaymentMethodType.CARD, result.getPaymentMethodType(), "Payment should be done via card");
        assertEquals(PaymentStatus.COMPLETE, result.getPaymentStatus(), "Payment should be marked as COMPLETE");

        assertEquals(201.3F, mockDB.getDriverDetails().get("D3").getEarnings(), 0.01, "Driver earnings should be updated correctly");
        assertTrue(mockDB.getPaymentDetails().containsKey("P-RIDE-001"), "Payment should be stored in database");
    }

    @Test
    void processUPIPayment() {
        paymentService.setPaymentMethod(PaymentMethodType.UPI);

        Payment result = paymentService.processPayment("RIDE-001");

        assertNotNull(result);
        assertEquals("P-RIDE-001", result.getPaymentID(), "Payment ID should be correctly generated");
        assertEquals("R1", result.getSenderID(), "Rider ID should match");
        assertEquals("D3", result.getReceiverID(), "Driver ID should match");
        assertEquals(201.3F, result.getAmount(), 0.01, "Payment amount should match the ride bill");
        assertEquals(PaymentMethodType.UPI, result.getPaymentMethodType(), "Payment should be done via UPI");
        assertEquals(PaymentStatus.COMPLETE, result.getPaymentStatus(), "Payment should be marked as COMPLETE");

        assertEquals(201.3F, mockDB.getDriverDetails().get("D3").getEarnings(), 0.01, "Driver earnings should be updated correctly");
        assertTrue(mockDB.getPaymentDetails().containsKey("P-RIDE-001"), "Payment should be stored in database");
    }

    @Test
    void processCashPayment() {
        paymentService.setPaymentMethod(PaymentMethodType.CASH);

        Payment result = paymentService.processPayment("RIDE-001");

        assertNotNull(result);
        assertEquals("P-RIDE-001", result.getPaymentID(), "Payment ID should be correctly generated");
        assertEquals("R1", result.getSenderID(), "Rider ID should match");
        assertEquals("D3", result.getReceiverID(), "Driver ID should match");
        assertEquals(201.3F, result.getAmount(), 0.01, "Payment amount should match the ride bill");
        assertEquals(PaymentMethodType.CASH, result.getPaymentMethodType(), "Payment should be done via cash");
        assertEquals(PaymentStatus.COMPLETE, result.getPaymentStatus(), "Payment should be marked as COMPLETE");

        assertEquals(201.3F, mockDB.getDriverDetails().get("D3").getEarnings(), 0.01, "Driver earnings should be updated correctly");
        assertTrue(mockDB.getPaymentDetails().containsKey("P-RIDE-001"), "Payment should be stored in database");
    }

    @Test
    void processWalletPayment() {
        paymentService.setPaymentMethod(PaymentMethodType.WALLET);

        Rider rider = new Rider("R1", 0, 0);
        rider.setWalletAmount(rider.getWalletAmount() + 500);
        mockDB.getRiderDetails().put("R1", rider);

        paymentService.processPayment("RIDE-001");

        assertEquals(201.3, mockDB.getDriverDetails().get("D3").getEarnings(), 0.1, "Earnings not updated at DB");
        assertEquals(298.7, rider.getWalletAmount(), 0.1, "Wallet amount has a mismatch");
    }
}