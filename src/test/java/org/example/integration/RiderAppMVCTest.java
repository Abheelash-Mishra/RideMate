package org.example.integration;

import org.example.repository.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig(locations = {
        "file:src/main/webapp/WEB-INF/spring/applicationContext.xml",
        "file:src/main/webapp/WEB-INF/spring/dispatcher-servlet.xml"
})
public class RiderAppMVCTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private Database db;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        db.reset();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void TestFullRideFlow() throws Exception {
        // Add drivers
        mockMvc.perform(post("/driver/add")
                        .param("driverID", "D1")
                        .param("x", "1")
                        .param("y", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "D2")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "D3")
                        .param("x", "2")
                        .param("y", "2"))
                .andExpect(status().isOk());

        // Add rider
        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "R1")
                        .param("x", "0")
                        .param("y", "0"))
                .andExpect(status().isCreated());

        // Match rider
        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "R1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchedDrivers").isArray());

        // Start ride
        mockMvc.perform(post("/ride/start")
                        .param("rideID", "RIDE-001")
                        .param("N", "2")
                        .param("riderID", "R1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideID").value("RIDE-001"))
                .andExpect(jsonPath("$.status").value("ONGOING"));

        // Stop ride
        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "RIDE-001")
                        .param("x", "4")
                        .param("y", "5")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideID").value("RIDE-001"))
                .andExpect(jsonPath("$.status").value("FINISHED"));

        // Get bill
        String response = mockMvc.perform(get("/ride/bill")
                        .param("rideID", "RIDE-001"))
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
                        .param("driverID", "D1")
                        .param("x", "0")
                        .param("y", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "D2")
                        .param("x", "2")
                        .param("y", "3"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "D3")
                        .param("x", "4")
                        .param("y", "2"))
                .andExpect(status().isOk());

        // Add riders
        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "R1")
                        .param("x", "3")
                        .param("y", "5"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "R2")
                        .param("x", "1")
                        .param("y", "1"))
                .andExpect(status().isCreated());

        // Match riders
        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "R1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchedDrivers").isArray());

        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "R2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchedDrivers").isArray());

        // Start rides
        mockMvc.perform(post("/ride/start")
                        .param("rideID", "RIDE-101")
                        .param("N", "1")
                        .param("riderID", "R1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideID").value("RIDE-101"))
                .andExpect(jsonPath("$.status").value("ONGOING"));

        mockMvc.perform(post("/ride/start")
                        .param("rideID", "RIDE-102")
                        .param("N", "1")
                        .param("riderID", "R2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideID").value("RIDE-102"))
                .andExpect(jsonPath("$.status").value("ONGOING"));

        // Stop rides
        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "RIDE-101")
                        .param("x", "10")
                        .param("y", "2")
                        .param("timeInMins", "48"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideID").value("RIDE-101"))
                .andExpect(jsonPath("$.status").value("FINISHED"));

        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "RIDE-102")
                        .param("x", "7")
                        .param("y", "9")
                        .param("timeInMins", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideID").value("RIDE-102"))
                .andExpect(jsonPath("$.status").value("FINISHED"));

        // Get bills
        String response = mockMvc.perform(get("/ride/bill").param("rideID", "RIDE-101"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(234.6f, Float.parseFloat(response), 0.1f);


        response = mockMvc.perform(get("/ride/bill").param("rideID", "RIDE-102"))
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
                        .param("driverID", "D1")
                        .param("x", "1")
                        .param("y", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "D2")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/driver/add")
                        .param("driverID", "D3")
                        .param("x", "2")
                        .param("y", "2"))
                .andExpect(status().isOk());

        // Add rider
        mockMvc.perform(post("/ride/rider/add")
                        .param("riderID", "R1")
                        .param("x", "0")
                        .param("y", "0"))
                .andExpect(status().isCreated());

        // Add money to rider's wallet
        String response = mockMvc.perform(post("/payment/add-money")
                        .param("riderID", "R1")
                        .param("amount", "520"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(520.0f, Float.parseFloat(response), 0.1f);


        // Match rider with drivers
        mockMvc.perform(get("/ride/rider/match")
                        .param("riderID", "R1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchedDrivers").isArray());

        // Start ride
        mockMvc.perform(post("/ride/start")
                        .param("rideID", "RIDE-001")
                        .param("N", "2")
                        .param("riderID", "R1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideID").value("RIDE-001"))
                .andExpect(jsonPath("$.status").value("ONGOING"));

        // Stop ride
        mockMvc.perform(post("/ride/stop")
                        .param("rideID", "RIDE-001")
                        .param("x", "4")
                        .param("y", "5")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideID").value("RIDE-001"))
                .andExpect(jsonPath("$.status").value("FINISHED"));

        // Get bill
        response = mockMvc.perform(get("/ride/bill")
                        .param("rideID", "RIDE-001"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(186.7f, Float.parseFloat(response), 0.1f);

        // Pay using wallet
        mockMvc.perform(post("/payment/pay")
                        .param("rideID", "RIDE-001")
                        .param("type", "WALLET"))
                .andExpect(status().isOk());

        // Check driver's earnings
        mockMvc.perform(get("/admin/drivers/earnings")
                        .param("driverID", "D3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverID").value("D3"))
                .andExpect(jsonPath("$.earnings").value("186.7"));
    }
}
