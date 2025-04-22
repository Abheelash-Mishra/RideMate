package org.example.integration;

import org.example.RiderAppCLI;
import org.example.repository.DriverRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.RideRepository;
import org.example.repository.RiderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CLITest {
    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayOutputStream testOutput;

    @Autowired
    private RiderAppCLI riderAppCLI;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    @BeforeEach
    void setUp() {
        testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));
    }

    @BeforeEach
    void resetDatabase() {
        rideRepository.deleteAll();
        riderRepository.deleteAll();
        driverRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
    }

    @Test
    void ShortTestcase() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                BILL 1
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                BILL 1 3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void LongerTestcase() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 0 1
                ADD_DRIVER d2@email.com 9876556789 2 3
                ADD_RIDER r1@email.com 9876556789 3 5
                ADD_DRIVER d4@email.com 9876556789 4 2
                ADD_RIDER r2@email.com 9876556789 1 1
                MATCH 1
                MATCH 2
                START_RIDE 1 1 Beach 10 2
                START_RIDE 1 2 Mall 7 9
                STOP_RIDE 1 48
                STOP_RIDE 2 50
                BILL 1
                BILL 2
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 2 3 1
                DRIVERS_MATCHED 1 2 3
                RIDE_STARTED 1
                RIDE_STARTED 2
                RIDE_STOPPED 1
                RIDE_STOPPED 2
                BILL 1 2 234.6
                BILL 2 1 258.0
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void RateDriver() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                BILL 1
                RATE_DRIVER 1 3 4.5 'Nice!'
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                BILL 1 3 186.7
                CURRENT_RATING 3 4.5
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingWallet() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                ADD_MONEY 1 520 UPI
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                BILL 1
                PAY 1 WALLET
                ADMIN_VIEW_DRIVER_EARNINGS 3
                """;

        String expectedOutput = """
                CURRENT_BALANCE 1 520.0
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                BILL 1 3 186.7
                PAID 3 186.7 VIA WALLET
                DRIVER_EARNINGS 3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingWalletWithLowBalance() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                ADD_MONEY 1 100 CARD
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                BILL 1
                PAY 1 WALLET
                """;

        String expectedOutput = """
                CURRENT_BALANCE 1 100.0
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                BILL 1 3 186.7
                LOW_BALANCE
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingCash() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                BILL 1
                PAY 1 CASH
                ADMIN_VIEW_DRIVER_EARNINGS 3
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                BILL 1 3 186.7
                PAID 3 186.7 VIA CASH
                DRIVER_EARNINGS 3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingCard() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                BILL 1
                PAY 1 CARD
                ADMIN_VIEW_DRIVER_EARNINGS 3
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                BILL 1 3 186.7
                PAID 3 186.7 VIA CARD
                DRIVER_EARNINGS 3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingUPI() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                BILL 1
                PAY 1 UPI
                ADMIN_VIEW_DRIVER_EARNINGS 3
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                BILL 1 3 186.7
                PAID 3 186.7 VIA UPI
                DRIVER_EARNINGS 3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void NoDriversAvailableAtAll_ThrowsException() {
        String input = """
                ADD_RIDER r1@email.com 9876556789 0 0
                ADD_RIDER r2@email.com 9876556789 0 0
                MATCH 1
                MATCH 2
                """;

        String expectedOutput = """
                NO_DRIVERS_AVAILABLE
                NO_DRIVERS_AVAILABLE
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StartRideWithBusyDriver_ThrowsException() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 0 1
                ADD_DRIVER d2@email.com 9876556789 2 3
                ADD_RIDER r1@email.com 9876556789 3 5
                ADD_DRIVER d4@email.com 9876556789 4 2
                ADD_RIDER r2@email.com 9876556789 1 1
                MATCH 1
                MATCH 2
                START_RIDE 1 1 Beach 10 2
                START_RIDE 2 2 Mall 5 7
                STOP_RIDE 1 48
                BILL 1
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 2 3 1
                DRIVERS_MATCHED 1 2 3
                RIDE_STARTED 1
                Invalid Ride, driver is already preoccupied with another ride
                RIDE_STOPPED 1
                BILL 1 2 234.6
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StartRideWithNonExistentDriver_ThrowsException() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 0 1
                ADD_DRIVER d2@email.com 9876556789 2 3
                ADD_RIDER r1@email.com 9876556789 3 5
                ADD_DRIVER d4@email.com 9876556789 4 2
                ADD_RIDER r2@email.com 9876556789 1 1
                MATCH 1
                MATCH 2
                START_RIDE 1 1 Beach 10 2
                START_RIDE 4 2 Mall 5 7
                STOP_RIDE 1 48
                BILL 1
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 2 3 1
                DRIVERS_MATCHED 1 2 3
                RIDE_STARTED 1
                Invalid Ride
                RIDE_STOPPED 1
                BILL 1 2 234.6
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StopAlreadyCompletedRide_ThrowsException() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                STOP_RIDE 1 10
                BILL 1
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                Invalid Ride Status - 1
                BILL 1 3 186.7""";

        runTest(input, expectedOutput);
    }

    @Test
    void StopNonExistentRide_ThrowsException() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                STOP_RIDE 999 10
                BILL 1
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                Invalid Ride ID - 999
                BILL 1 3 186.7""";

        runTest(input, expectedOutput);
    }

    @Test
    void TestAdminCommands() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                BILL 1
                RATE_DRIVER 1 3 4.5 'Drove well!'
                ADMIN_REMOVE_DRIVER 2
                ADMIN_LIST_DRIVERS 2
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                BILL 1 3 186.7
                CURRENT_RATING 3 4.5
                REMOVED_DRIVER 2
                DRIVER_1 (X=1, Y=1) RATING 0.0
                DRIVER_3 (X=2, Y=2) RATING 4.5""";

        runTest(input, expectedOutput);
    }

    @Test
    void DeleteNonExistentDriver_ThrowsException() {
        String input = """
                ADD_DRIVER d1@email.com 9876556789 1 1
                ADD_DRIVER d1@email.com 9876556789 4 5
                ADD_DRIVER d1@email.com 9876556789 2 2
                ADD_RIDER r1@email.com 9876556789 0 0
                MATCH 1
                START_RIDE 2 1 Beach 4 5
                STOP_RIDE 1 32
                BILL 1
                ADMIN_REMOVE_DRIVER 99
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 1 3
                RIDE_STARTED 1
                RIDE_STOPPED 1
                BILL 1 3 186.7
                Invalid Driver ID - 99
                """;

        runTest(input, expectedOutput);
    }


    private void runTest(String input, String expectedOutput) {
        ByteArrayInputStream testInput = new ByteArrayInputStream(input.getBytes());
        System.setIn(testInput);

        riderAppCLI.reset();
        riderAppCLI.start();

        StringBuilder finalOutput = new StringBuilder();

        String output = testOutput.toString().trim();
        String[] outputLines = output.split("\n", 2);
        outputLines = String.join(" ", outputLines[1]).split("\n");

        for (String line : outputLines) {
            line = line.trim();
            int lastColonIndex = line.lastIndexOf(": ");

            if (lastColonIndex != -1) {
                String logMessage = line.substring(lastColonIndex + 2).trim();
                finalOutput.append(logMessage).append("\n");
            }
        }

        for (String expectedLine : expectedOutput.split("\n")) {
            assertThat(finalOutput.toString(), containsString(expectedLine));
        }
    }

}