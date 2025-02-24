package org.example.unit;

import org.example.repository.Database;

import org.example.models.Driver;
import org.example.models.Ride;
import org.example.models.Rider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.services.payment.PaymentMethodType;
import org.example.services.payment.PaymentService;
import org.example.utils.TestUtils;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {
    private Database mockDB;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        mockDB = mock(Database.class);

        HashMap<String, Ride> rides = new HashMap<>();
        HashMap<String, Driver> drivers = new HashMap<>();
        HashMap<String, Rider> riders = new HashMap<>();

        when(mockDB.getRideDetails()).thenReturn(rides);
        when(mockDB.getDriverDetails()).thenReturn(drivers);
        when(mockDB.getRiderDetails()).thenReturn(riders);

        drivers.put("D3", new Driver(2, 2));

        Ride ride = new Ride("R1", "D3");
        ride.finishRide(5, 5, 20);
        ride.setBill(201.3F);
        rides.put("RIDE-001", ride);
    }

    @Test
    void processCardPayment() {
        paymentService = new PaymentService(mockDB);

        TestUtils.captureOutput();
        paymentService.processPayment("RIDE-001");
        String output = TestUtils.getCapturedOutput();

        assertTrue(output.contains("PAID D3 201.3 VIA CARD"), "Card payment went wrong");
        assertEquals(201.3F, mockDB.getDriverDetails().get("D3").getEarnings(), 0.1, "Earnings not updated at DB");
    }

    @Test
    void processUPIPayment() {
        paymentService = new PaymentService(mockDB);

        TestUtils.captureOutput();
        paymentService.processPayment("RIDE-001");
        String output = TestUtils.getCapturedOutput();

        assertTrue(output.contains("PAID D3 201.3 VIA UPI"), "UPI payment went wrong");
        assertEquals(201.3F, mockDB.getDriverDetails().get("D3").getEarnings(), 0.1, "Earnings not updated at DB");
    }

    @Test
    void processCashPayment() {
        paymentService = new PaymentService(mockDB);

        TestUtils.captureOutput();
        paymentService.processPayment("RIDE-001");
        String output = TestUtils.getCapturedOutput();

        assertTrue(output.contains("PAID D3 201.3 VIA CASH"), "Cash payment went wrong");
        assertEquals(201.3F, mockDB.getDriverDetails().get("D3").getEarnings(), 0.1, "Earnings not updated at DB");
    }

    @Test
    void processWalletPayment() {
        paymentService = new PaymentService(mockDB);

        Rider rider = new Rider(0, 0);
        rider.addMoney(500);
        mockDB.getRiderDetails().put("R1", rider);

        paymentService.processPayment("RIDE-001");

        assertEquals(201.3, mockDB.getDriverDetails().get("D3").getEarnings(), 0.1, "Earnings not updated at DB");
        assertEquals(298.7, rider.getWalletAmount(), 0.1, "Wallet amount has a mismatch");
    }
}