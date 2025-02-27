package org.example.integration;

import org.example.repository.Database;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig(locations = "file:src/main/webapp/WEB-INF/spring/applicationContext.xml")
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
    public void shortTestcase() throws Exception {
        String input = """
            POST /driver/add?driverID=D1&x=1&y=1
            POST /driver/add?driverID=D2&x=4&y=5
            POST /driver/add?driverID=D3&x=2&y=2
            POST /ride/rider/add?riderID=R1&x=0&y=0
            GET /ride/rider/match?riderID=R1
            POST /ride/start?rideID=RIDE-001&N=2&riderID=R1
            POST /ride/stop?rideID=RIDE-001&x=4&y=5&timeInMins=32
            GET /ride/bill?rideID=RIDE-001
            """;

        String expectedOutput = """
            DRIVERS_MATCHED D1 D3
            RIDE_STARTED RIDE-001
            RIDE_STOPPED RIDE-001
            BILL RIDE-001 186.7
            """;

        runTest(input, expectedOutput);
    }

    @Test
    public void LongerTestcase() throws Exception {
        String input = """
            POST /driver/add?driverID=D1&x=0&y=1
            POST /driver/add?driverID=D2&x=2&y=3
            POST /ride/rider/add?riderID=R1&x=3&y=5
            POST /driver/add?driverID=D3&x=4&y=2
            POST /ride/rider/add?riderID=R2&x=1&y=1
            GET /ride/rider/match?riderID=R1
            GET /ride/rider/match?riderID=R2
            POST /ride/start?rideID=RIDE-101&N=1&riderID=R1
            POST /ride/start?rideID=RIDE-102&N=1&riderID=R2
            POST /ride/stop?rideID=RIDE-101&x=10&y=2&timeInMins=48
            POST /ride/stop?rideID=RIDE-102&x=7&y=9&timeInMins=50
            GET /ride/bill?rideID=RIDE-101
            GET /ride/bill?rideID=RIDE-102
            """;

        String expectedOutput = """
            DRIVERS_MATCHED D2 D3 D1
            DRIVERS_MATCHED D1 D2 D3
            RIDE_STARTED RIDE-101
            RIDE_STARTED RIDE-102
            RIDE_STOPPED RIDE-101
            RIDE_STOPPED RIDE-102
            BILL RIDE-101 234.6
            BILL RIDE-102 258.0
            """;

        runTest(input, expectedOutput);
    }

    @Test
    public void BillRiderUsingWallet() throws Exception {
        String input = """
            POST /driver/add?driverID=D1&x=1&y=1
            POST /driver/add?driverID=D2&x=4&y=5
            POST /driver/add?driverID=D3&x=2&y=2
            POST /ride/rider/add?riderID=R1&x=0&y=0
            POST /payment/add-money?riderID=R1&amount=520
            GET /ride/rider/match?riderID=R1
            POST /ride/start?rideID=RIDE-001&N=2&riderID=R1
            POST /ride/stop?rideID=RIDE-001&x=4&y=5&timeInMins=32
            GET /ride/bill?rideID=RIDE-001
            POST /payment/pay?rideID=RIDE-001&type=WALLET
            GET /admin/drivers/earnings?driverID=D3
            """;

        String expectedOutput = """
            CURRENT_BALANCE R1 520.0
            DRIVERS_MATCHED D1 D3
            RIDE_STARTED RIDE-001
            RIDE_STOPPED RIDE-001
            BILL RIDE-001 186.7
            PAID 186.7 SUCCESSFULLY | CURRENT_BALANCE 333.3
            DRIVER_EARNINGS D3 186.7
            """;

        runTest(input, expectedOutput);
    }



    private void runTest(String input, String expectedOutput) throws Exception {
        StringBuilder actualOutput = new StringBuilder();

        String[] commands = input.split("\n");
        for (String command : commands) {
            if (command.trim().isEmpty()) continue;

            String[] parts = command.split(" ");
            String method = parts[0];
            String url = parts[1];

            String response = performRequest(method, url);
            actualOutput.append(response).append("\n");
        }

        Assertions.assertEquals(expectedOutput.trim(), actualOutput.toString().trim());
    }

    private String performRequest(String method, String url) throws Exception {
        switch (method) {
            case "POST":
                return mockMvc.perform(MockMvcRequestBuilders.post(url))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
            case "GET":
                return mockMvc.perform(MockMvcRequestBuilders.get(url))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }
}
