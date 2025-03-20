package org.example.integration;

import org.example.RiderAppCLI;
import org.example.repository.DriverRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.RideRepository;
import org.example.repository.RiderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

@SpringBootTest
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
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
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
                ADD_DRIVER 1 0 1
                ADD_DRIVER 2 2 3
                ADD_RIDER 1 3 5
                ADD_DRIVER 3 4 2
                ADD_RIDER 2 1 1
                MATCH 1
                MATCH 2
                START_RIDE 101 1 1
                START_RIDE 102 1 2
                STOP_RIDE 101 10 2 48
                STOP_RIDE 102 7 9 50
                BILL 101
                BILL 102
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 2 3 1
                DRIVERS_MATCHED 1 2 3
                RIDE_STARTED 101
                RIDE_STARTED 102
                RIDE_STOPPED 101
                RIDE_STOPPED 102
                BILL 101 2 234.6
                BILL 102 1 258.0
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void RateDriver() {
        String input = """
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
                BILL 1
                RATE_DRIVER 3 4.5
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
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                ADD_MONEY 1 520
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
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
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                ADD_MONEY 1 100
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
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
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                ADD_MONEY 1 520
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
                BILL 1
                PAY 1 CASH
                ADMIN_VIEW_DRIVER_EARNINGS 3
                """;

        String expectedOutput = """
                CURRENT_BALANCE 1 520.0
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
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                ADD_MONEY 1 520
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
                BILL 1
                PAY 1 CARD
                ADMIN_VIEW_DRIVER_EARNINGS 3
                """;

        String expectedOutput = """
                CURRENT_BALANCE 1 520.0
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
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                ADD_MONEY 1 520
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
                BILL 1
                PAY 1 UPI
                ADMIN_VIEW_DRIVER_EARNINGS 3
                """;

        String expectedOutput = """
                CURRENT_BALANCE 1 520.0
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
                ADD_RIDER 1 3 5
                ADD_RIDER 2 1 1
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
    void StartExistingRide_ThrowsException() {
        String input = """
                ADD_DRIVER 1 0 1
                ADD_DRIVER 2 2 3
                ADD_RIDER 1 3 5
                ADD_DRIVER 3 4 2
                ADD_RIDER 2 1 1
                MATCH 1
                MATCH 2
                START_RIDE 101 1 1
                START_RIDE 101 1 2
                STOP_RIDE 101 10 2 48
                BILL 101
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 2 3 1
                DRIVERS_MATCHED 1 2 3
                RIDE_STARTED 101
                Invalid Ride ID - 101
                RIDE_STOPPED 101
                BILL 101 2 234.6
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StartRideWithBusyDriver_ThrowsException() {
        String input = """
                ADD_DRIVER 1 0 1
                ADD_DRIVER 2 2 3
                ADD_RIDER 1 3 5
                ADD_DRIVER 3 4 2
                ADD_RIDER 2 1 1
                MATCH 1
                MATCH 2
                START_RIDE 101 1 1
                START_RIDE 102 2 2
                STOP_RIDE 101 10 2 48
                BILL 101
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 2 3 1
                DRIVERS_MATCHED 1 2 3
                RIDE_STARTED 101
                Invalid Ride - 102
                RIDE_STOPPED 101
                BILL 101 2 234.6
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StartRideWithNonExistentDriver_ThrowsException() {
        String input = """
                ADD_DRIVER 1 0 1
                ADD_DRIVER 2 2 3
                ADD_RIDER 1 3 5
                ADD_DRIVER 3 4 2
                ADD_RIDER 2 1 1
                MATCH 1
                MATCH 2
                START_RIDE 101 1 1
                START_RIDE 102 4 2
                STOP_RIDE 101 10 2 48
                BILL 101
                """;

        String expectedOutput = """
                DRIVERS_MATCHED 2 3 1
                DRIVERS_MATCHED 1 2 3
                RIDE_STARTED 101
                Invalid Ride - 102
                RIDE_STOPPED 101
                BILL 101 2 234.6
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StopAlreadyCompletedRide_ThrowsException() {
        String input = """
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
                STOP_RIDE 1 1 1 10
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
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
                STOP_RIDE 999 1 1 10
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
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
                BILL 1
                RATE_DRIVER 3 4.5
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
                ADD_DRIVER 1 1 1
                ADD_DRIVER 2 4 5
                ADD_DRIVER 3 2 2
                ADD_RIDER 1 0 0
                MATCH 1
                START_RIDE 1 2 1
                STOP_RIDE 1 4 5 32
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