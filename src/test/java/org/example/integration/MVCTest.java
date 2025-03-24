package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.*;
import org.example.models.PaymentMethodType;
import org.example.models.PaymentStatus;
import org.example.models.RideStatus;
import org.example.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MVCTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    public void setup() {
        rideRepository.deleteAll();
        riderRepository.deleteAll();
        driverRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    public void TestFullRideFlow() throws Exception {
        // Add drivers
        mockMvc.perform(post("/driver/add")
                        .param("driverID", "1")
                        .param("x", "1")
                        .param("y", "1"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "2")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "3")
                        .param("x", "2")
                        .param("y", "2"))
                .andExpect(status().isCreated());


        // Add rider
        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "1")
                        .param("x", "0")
                        .param("y", "0"))
                .andExpect(status().isCreated());


        // Match rider
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 3L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start ride
        RideStatusDTO expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .param("rideID", "1")
                        .param("N", "2")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop ride
        expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "1")
                        .param("x", "4")
                        .param("y", "5")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));

        // Get bill
        String response = mockMvc.perform(get("/ride/bill")
                        .param("rideID", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        float billAmount = Float.parseFloat(response);
        assertEquals(186.7f, billAmount, 0.1f);
    }


    @Test
    public void TestMultipleRidersFlow() throws Exception {
        // Add drivers
        mockMvc.perform(post("/driver/add")
                        .param("driverID", "1")
                        .param("x", "0")
                        .param("y", "1"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "2")
                        .param("x", "2")
                        .param("y", "3"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "3")
                        .param("x", "4")
                        .param("y", "2"))
                .andExpect(status().isCreated());


        // Add riders
        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "1")
                        .param("x", "3")
                        .param("y", "5"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "2")
                        .param("x", "1")
                        .param("y", "1"))
                .andExpect(status().isCreated());


        // Match riders
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(2L, 3L, 1L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 2L, 3L));
        expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start rides
        RideStatusDTO expectedRideStatus = new RideStatusDTO(101, 1, 2, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .param("rideID", "101")
                        .param("N", "1")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        expectedRideStatus = new RideStatusDTO(102, 2, 1, RideStatus.ONGOING);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .param("rideID", "102")
                        .param("N", "1")
                        .param("riderID", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop rides
        expectedRideStatus = new RideStatusDTO(101, 1, 2, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "101")
                        .param("x", "10")
                        .param("y", "2")
                        .param("timeInMins", "48"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        expectedRideStatus = new RideStatusDTO(102, 2, 1, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "102")
                        .param("x", "7")
                        .param("y", "9")
                        .param("timeInMins", "50"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Get bills
        String response = mockMvc.perform(get("/ride/bill")
                        .param("rideID", "101"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(234.6f, Float.parseFloat(response), 0.1f);


        response = mockMvc.perform(get("/ride/bill")
                        .param("rideID", "102"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(258.0f, Float.parseFloat(response), 0.1f);
    }

    @Test
    public void BillRiderUsingWallet() throws Exception {
        // Add drivers
        mockMvc.perform(post("/driver/add")
                        .param("driverID", "1")
                        .param("x", "1")
                        .param("y", "1"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "2")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "3")
                        .param("x", "2")
                        .param("y", "2"))
                .andExpect(status().isCreated());


        // Add rider
        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "1")
                        .param("x", "0")
                        .param("y", "0"))
                .andExpect(status().isCreated());


        // Add money to rider's wallet
        String response = mockMvc.perform(post("/payment/add-money")
                        .param("riderID", "1")
                        .param("amount", "520"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(520.0f, Float.parseFloat(response), 0.1f);


        // Match rider with drivers
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 3L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start ride
        RideStatusDTO expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .param("rideID", "1")
                        .param("N", "2")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop ride
        expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "1")
                        .param("x", "4")
                        .param("y", "5")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Get bill
        response = mockMvc.perform(get("/ride/bill")
                        .param("rideID", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(186.7f, Float.parseFloat(response), 0.1f);


        // Pay using wallet
        PaymentDetailsDTO expectedPaymentDetails = new PaymentDetailsDTO(
                2,
                1,
                3,
                186.7f,
                PaymentMethodType.WALLET,
                PaymentStatus.COMPLETE
        );
        String expectedPaymentDetailsJson = new ObjectMapper().writeValueAsString(expectedPaymentDetails);

        mockMvc.perform(post("/payment/pay")
                        .param("rideID", "1")
                        .param("type", "WALLET"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedPaymentDetailsJson));


        // Check driver's earnings
        DriverEarningsDTO expectedDriverEarnings = new DriverEarningsDTO(3, 186.7f);
        String expectedDriverEarningsJson = new ObjectMapper().writeValueAsString(expectedDriverEarnings);

        mockMvc.perform(get("/admin/drivers/earnings")
                        .param("driverID", "3"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedDriverEarningsJson));
    }


    @Test
    public void BillRiderUsingWalletWithLowBalance() throws Exception {
        // Add drivers
        mockMvc.perform(post("/driver/add")
                        .param("driverID", "1")
                        .param("x", "1")
                        .param("y", "1"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "2")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "3")
                        .param("x", "2")
                        .param("y", "2"))
                .andExpect(status().isCreated());


        // Add rider
        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "1")
                        .param("x", "0")
                        .param("y", "0"))
                .andExpect(status().isCreated());


        // Add money to rider's wallet
        String response = mockMvc.perform(post("/payment/add-money")
                        .param("riderID", "1")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(100.0f, Float.parseFloat(response), 0.1f);


        // Match rider with drivers
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 3L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start ride
        RideStatusDTO expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .param("rideID", "1")
                        .param("N", "2")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop ride
        expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "1")
                        .param("x", "4")
                        .param("y", "5")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Get bill
        response = mockMvc.perform(get("/ride/bill")
                        .param("rideID", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(186.7f, Float.parseFloat(response), 0.1f);


        // Pay using wallet
        PaymentDetailsDTO expectedPaymentDetails = new PaymentDetailsDTO(
                1,
                1,
                3,
                186.7f,
                PaymentMethodType.WALLET,
                PaymentStatus.FAILED
        );
        String expectedPaymentDetailsJson = new ObjectMapper().writeValueAsString(expectedPaymentDetails);

        mockMvc.perform(post("/payment/pay")
                        .param("rideID", "1")
                        .param("type", "WALLET"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedPaymentDetailsJson));
    }


    @Test
    public void TestAdminEndpoints() throws Exception {
        // Add drivers
        mockMvc.perform(post("/driver/add")
                        .param("driverID", "1")
                        .param("x", "1")
                        .param("y", "1"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "2")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "3")
                        .param("x", "2")
                        .param("y", "2"))
                .andExpect(status().isCreated());


        // Add rider
        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "1")
                        .param("x", "0")
                        .param("y", "0"))
                .andExpect(status().isCreated());


        // Match rider
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 3L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start ride
        RideStatusDTO expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .param("rideID", "1")
                        .param("N", "2")
                        .param("riderID", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop ride
        expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "1")
                        .param("x", "4")
                        .param("y", "5")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));

        // Get bill
        String response = mockMvc.perform(get("/ride/bill")
                        .param("rideID", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        float billAmount = Float.parseFloat(response);
        assertEquals(186.7f, billAmount, 0.1f);


        // Rate driver
        DriverRatingDTO expectedDriverRating = new DriverRatingDTO(3, 4.5f);
        String expectedDriverRatingJson = new ObjectMapper().writeValueAsString(expectedDriverRating);

        mockMvc.perform(post("/driver/rate")
                        .param("driverID", "3")
                        .param("rating", "4.5"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedDriverRatingJson));


        // Delete driver
        mockMvc.perform(delete("/admin/drivers/remove")
                        .param("driverID", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));


        // List first N drivers from database
        List<DriverDTO> expectedDrivers = List.of(
                new DriverDTO(1, 1, 1, 0.0),
                new DriverDTO(3, 2, 2, 4.5)
        );
        String expectedDriversJson = new ObjectMapper().writeValueAsString(expectedDrivers);

        mockMvc.perform(get("/admin/drivers/list")
                        .param("N", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedDriversJson));
    }
}
