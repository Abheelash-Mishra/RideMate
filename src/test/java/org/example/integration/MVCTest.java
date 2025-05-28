package org.example.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.TestConfig;
import org.example.dto.*;
import org.example.models.*;
import org.example.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MVCTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String TEST_ADMIN_EMAIL = "admin@gmail.com";
    private final String TEST_ADMIN_PASSWORD = "admin@test";

    @BeforeEach
    public void setup() {
        rideRepository.deleteAll();
        riderRepository.deleteAll();
        driverRepository.deleteAll();
        paymentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void TestFullRideFlow() throws Exception {
        // Register drivers
        RegisterRequest registerRequest;
        registerRequest = new RegisterRequest("d1@email.com", "test@d1", "9876556789", "Main St", 1, 1, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d2@email.com", "test@d2", "9876556789", "Main St", 4, 5, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d3@email.com", "test@d3", "9876556789", "Main St", 2, 2, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Register rider
        registerRequest = new RegisterRequest("r1@email.com", "test@r1", "9876556789", "Main St", 0, 0, "Rider");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Log in rider
        LoginRequest loginRequest = new LoginRequest("r1@email.com", "test@r1");
        String json = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json);
        String jwt = node.get("token").asText();


        // Match rider
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 3L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start ride
        RideStatusDTO expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .header("Authorization", "Bearer " + jwt)
                        .param("N", "2")
                        .param("destination", "Beach")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop ride
        expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .header("Authorization", "Bearer " + jwt)
                        .param("rideID", "1")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));

        // Get bill
        String response = mockMvc.perform(get("/ride/bill")
                        .header("Authorization", "Bearer " + jwt)
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
        // Register drivers
        RegisterRequest registerRequest;
        registerRequest = new RegisterRequest("d1@email.com", "test@d1", "9876556789", "Main St", 0, 1, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d2@email.com", "test@d2", "9876556789", "Main St", 2, 3, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d3@email.com", "test@d3", "9876556789", "Main St", 4, 2, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Register rider
        registerRequest = new RegisterRequest("r1@email.com", "test@r1", "9876556789", "Main St", 3, 5, "Rider");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("r2@email.com", "test@r2", "9876556789", "Main St", 1, 1, "Rider");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Log in riders
        LoginRequest loginRequest = new LoginRequest("r1@email.com", "test@r1");
        String json1 = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json1);
        String r1_jwt = node.get("token").asText();

        loginRequest = new LoginRequest("r2@email.com", "test@r2");
        String json2 = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        node = objectMapper.readTree(json2);
        String r2_jwt = node.get("token").asText();


        // Match riders
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(2L, 3L, 1L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .header("Authorization", "Bearer " + r1_jwt))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 2L, 3L));
        expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .header("Authorization", "Bearer " + r2_jwt))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start rides
        RideStatusDTO expectedRideStatus = new RideStatusDTO(1, 1, 2, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .header("Authorization", "Bearer " + r1_jwt)
                        .param("N", "1")
                        .param("destination", "Beach")
                        .param("x", "10")
                        .param("y", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        expectedRideStatus = new RideStatusDTO(2, 2, 1, RideStatus.ONGOING);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .header("Authorization", "Bearer " + r2_jwt)
                        .param("N", "1")
                        .param("destination", "Beach")
                        .param("x", "7")
                        .param("y", "9"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop rides
        expectedRideStatus = new RideStatusDTO(1, 1, 2, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .header("Authorization", "Bearer " + r1_jwt)
                        .param("rideID", "1")
                        .param("timeInMins", "48"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        expectedRideStatus = new RideStatusDTO(2, 2, 1, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .header("Authorization", "Bearer " + r2_jwt)
                        .param("rideID", "2")
                        .param("timeInMins", "50"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Get bills
        String response = mockMvc.perform(get("/ride/bill")
                        .header("Authorization", "Bearer " + r1_jwt)
                        .param("rideID", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(234.6f, Float.parseFloat(response), 0.1f);


        response = mockMvc.perform(get("/ride/bill")
                        .header("Authorization", "Bearer " + r2_jwt)
                        .param("rideID", "2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(258.0f, Float.parseFloat(response), 0.1f);
    }

    @Test
    public void BillRiderUsingWallet() throws Exception {
        // Register drivers
        RegisterRequest registerRequest;
        registerRequest = new RegisterRequest("d1@email.com", "test@d1", "9876556789", "Main St", 1, 1, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d2@email.com", "test@d2", "9876556789", "Main St", 4, 5, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d3@email.com", "test@d3", "9876556789", "Main St", 2, 2, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Register rider
        registerRequest = new RegisterRequest("r1@email.com", "test@r1", "9876556789", "Main St", 0, 0, "Rider");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Log in rider
        LoginRequest loginRequest = new LoginRequest("r1@email.com", "test@r1");
        String json = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json);
        String jwt = node.get("token").asText();


        // Add money to rider's wallet
        String response = mockMvc.perform(post("/payment/add-money")
                        .header("Authorization", "Bearer " + jwt)
                        .param("amount", "520")
                        .param("type", "UPI"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(520.0f, Float.parseFloat(response), 0.1f);


        // Match rider
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 3L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start ride
        RideStatusDTO expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .header("Authorization", "Bearer " + jwt)
                        .param("N", "2")
                        .param("destination", "Beach")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop ride
        expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .header("Authorization", "Bearer " + jwt)
                        .param("rideID", "1")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));

        // Get bill
        response = mockMvc.perform(get("/ride/bill")
                        .header("Authorization", "Bearer " + jwt)
                        .param("rideID", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        float billAmount = Float.parseFloat(response);
        assertEquals(186.7f, billAmount, 0.1f);


        // Pay using wallet
        PaymentDetailsDTO expectedPaymentDetails = new PaymentDetailsDTO(
                1,
                1,
                3,
                186.7f,
                PaymentMethodType.WALLET,
                PaymentStatus.COMPLETE
        );
        String expectedPaymentDetailsJson = new ObjectMapper().writeValueAsString(expectedPaymentDetails);

        mockMvc.perform(post("/payment/pay")
                        .header("Authorization", "Bearer " + jwt)
                        .param("rideID", "1")
                        .param("type", "WALLET"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedPaymentDetailsJson));
    }

    @Test
    public void BillRiderUsingWalletWithLowBalance() throws Exception {
        // Register drivers
        RegisterRequest registerRequest;
        registerRequest = new RegisterRequest("d1@email.com", "test@d1", "9876556789", "Main St", 1, 1, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d2@email.com", "test@d2", "9876556789", "Main St", 4, 5, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d3@email.com", "test@d3", "9876556789", "Main St", 2, 2, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Register rider
        registerRequest = new RegisterRequest("r1@email.com", "test@r1", "9876556789", "Main St", 0, 0, "Rider");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Log in rider
        LoginRequest loginRequest = new LoginRequest("r1@email.com", "test@r1");
        String json = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json);
        String jwt = node.get("token").asText();


        // Add money to rider's wallet
        String response = mockMvc.perform(post("/payment/add-money")
                        .header("Authorization", "Bearer " + jwt)
                        .param("amount", "100")
                        .param("type", "UPI"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(100.0f, Float.parseFloat(response), 0.1f);


        // Match rider
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 3L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start ride
        RideStatusDTO expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .header("Authorization", "Bearer " + jwt)
                        .param("N", "2")
                        .param("destination", "Beach")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop ride
        expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .header("Authorization", "Bearer " + jwt)
                        .param("rideID", "1")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));

        // Get bill
        response = mockMvc.perform(get("/ride/bill")
                        .header("Authorization", "Bearer " + jwt)
                        .param("rideID", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        float billAmount = Float.parseFloat(response);
        assertEquals(186.7f, billAmount, 0.1f);


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
                        .header("Authorization", "Bearer " + jwt)
                        .param("rideID", "1")
                        .param("type", "WALLET"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedPaymentDetailsJson));
    }

    @Test
    public void UseRiderServicesAsDriver() throws Exception {
        // Register driver
        RegisterRequest registerRequest;
        registerRequest = new RegisterRequest("d1@email.com", "test@d1", "9876556789", "Main St", 1, 1, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Log in as driver
        LoginRequest loginRequest = new LoginRequest("d1@email.com", "test@d1");
        String json = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json);
        String jwt = node.get("token").asText();


        // Attempt to use rider services as driver
        mockMvc.perform(get("/ride/rider/match")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isForbidden());
    }

    @Test
    public void UnauthorizedAttemptsToAccessAdminServices() throws Exception {
        // Register
        RegisterRequest registerRequest;
        registerRequest = new RegisterRequest("d1@email.com", "test@d1", "9876556789", "Main St", 1, 1, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("r1@email.com", "test@r1", "9876556789", "Main St", 1, 1, "Rider");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Log in
        LoginRequest loginRequest;
        loginRequest = new LoginRequest("d1@email.com", "test@d1");
        String json_d1 = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json_d1);
        String jwt_d1 = node.get("token").asText();

        loginRequest = new LoginRequest("r1@email.com", "test@r1");
        String json_r1 = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        node = objectMapper.readTree(json_r1);
        String jwt_r1 = node.get("token").asText();


        // Attempt to use admin services
        mockMvc.perform(get("/admin/drivers/remove")
                        .header("Authorization", "Bearer " + jwt_d1)
                        .param("driverID", "2"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/drivers/remove")
                        .header("Authorization", "Bearer " + jwt_r1)
                        .param("driverID", "2"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void TestAdminEndpoints() throws Exception {
        // Register drivers
        RegisterRequest registerRequest;
        registerRequest = new RegisterRequest("d1@email.com", "test@d1", "9876556789", "Main St", 1, 1, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d2@email.com", "test@d2", "9876556789", "Main St", 4, 5, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        registerRequest = new RegisterRequest("d3@email.com", "test@d3", "9876556789", "Main St", 2, 2, "Driver");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Register rider
        registerRequest = new RegisterRequest("r1@email.com", "test@r1", "9876556789", "Main St", 0, 0, "Rider");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());


        // Log in rider
        LoginRequest loginRequest = new LoginRequest("r1@email.com", "test@r1");
        String json = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json);
        String jwt = node.get("token").asText();


        // Match rider
        MatchedDriversDTO expectedMatchedDrivers = new MatchedDriversDTO(List.of(1L, 3L));
        String expectedMatchedDriversJson = new ObjectMapper().writeValueAsString(expectedMatchedDrivers);

        mockMvc.perform(get("/ride/rider/match")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedMatchedDriversJson));


        // Start ride
        RideStatusDTO expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.ONGOING);
        String expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/start")
                        .header("Authorization", "Bearer " + jwt)
                        .param("N", "2")
                        .param("destination", "Beach")
                        .param("x", "4")
                        .param("y", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));


        // Stop ride
        expectedRideStatus = new RideStatusDTO(1, 1, 3, RideStatus.FINISHED);
        expectedRideStatusJson = new ObjectMapper().writeValueAsString(expectedRideStatus);

        mockMvc.perform(post("/ride/stop")
                        .header("Authorization", "Bearer " + jwt)
                        .param("rideID", "1")
                        .param("timeInMins", "32"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedRideStatusJson));

        // Get bill
        String response = mockMvc.perform(get("/ride/bill")
                        .header("Authorization", "Bearer " + jwt)
                        .param("rideID", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        float billAmount = Float.parseFloat(response);
        assertEquals(186.7f, billAmount, 0.1f);

        if (userRepository.findByEmail(TEST_ADMIN_EMAIL).isEmpty()) {
            User adminUser = new User();
            adminUser.setEmail(TEST_ADMIN_EMAIL);
            adminUser.setPassword(passwordEncoder.encode(TEST_ADMIN_PASSWORD));
            adminUser.setRole(Role.ADMIN);

            userRepository.save(adminUser);
        }

        loginRequest = new LoginRequest("admin@gmail.com", "admin@test");
        String json_admin = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        node = objectMapper.readTree(json_admin);
        String jwt_admin = node.get("token").asText();


        // Delete driver
        mockMvc.perform(delete("/admin/drivers/remove")
                        .header("Authorization", "Bearer " + jwt_admin)
                        .param("driverID", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));


        // List first N drivers from database
        List<DriverDTO> expectedDrivers = List.of(
                new DriverDTO(1, 1, 1, 0.0),
                new DriverDTO(3, 2, 2, 0.0)
        );
        String expectedDriversJson = new ObjectMapper().writeValueAsString(expectedDrivers);

        mockMvc.perform(get("/admin/drivers/list")
                        .header("Authorization", "Bearer " + jwt_admin)
                        .param("N", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedDriversJson));
    }
}
