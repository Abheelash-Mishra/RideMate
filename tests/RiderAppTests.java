import main.RiderApp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

class RiderAppTests {
    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayOutputStream testOutput;

    @BeforeEach
    void setUp() {
        testOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOutput));
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
    }

    @Test
    void ShortTestcase() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void LongerTestcase() {
        String input = """
                ADD_DRIVER D1 0 1
                ADD_DRIVER D2 2 3
                ADD_RIDER R1 3 5
                ADD_DRIVER D3 4 2
                ADD_RIDER R2 1 1
                MATCH R1
                MATCH R2
                START_RIDE RIDE-101 1 R1
                START_RIDE RIDE-102 1 R2
                STOP_RIDE RIDE-101 10 2 48
                STOP_RIDE RIDE-102 7 9 50
                BILL RIDE-101
                BILL RIDE-102
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D2 D3 D1
                DRIVERS_MATCHED D1 D2 D3
                RIDE_STARTED RIDE-101
                RIDE_STARTED RIDE-102
                RIDE_STOPPED RIDE-101
                RIDE_STOPPED RIDE-102
                BILL RIDE-101 D2 234.6
                BILL RIDE-102 D1 258.0
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void RateDriver() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                RATE_DRIVER D3 4.5
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                CURRENT_RATING D3 4.5
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingWallet() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                ADD_MONEY R1 520
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                PAY RIDE-001 WALLET
                ADMIN_VIEW_DRIVER_EARNINGS D3
                """;

        String expectedOutput = """
                CURRENT_BALANCE R1 520.0
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                PAID 186.7 SUCCESSFULLY | CURRENT_BALANCE 333.3
                DRIVER_EARNINGS D3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingWalletWithLowBalance() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                ADD_MONEY R1 100
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                PAY RIDE-001 WALLET
                """;

        String expectedOutput = """
                CURRENT_BALANCE R1 100.0
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                LOW_BALANCE
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingCash() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                ADD_MONEY R1 520
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                PAY RIDE-001 CASH
                ADMIN_VIEW_DRIVER_EARNINGS D3
                """;

        String expectedOutput = """
                CURRENT_BALANCE R1 520.0
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                PAID D3 186.7 VIA CASH
                DRIVER_EARNINGS D3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingCard() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                ADD_MONEY R1 520
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                PAY RIDE-001 CARD
                ADMIN_VIEW_DRIVER_EARNINGS D3
                """;

        String expectedOutput = """
                CURRENT_BALANCE R1 520.0
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                PAID D3 186.7 VIA CARD
                DRIVER_EARNINGS D3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void BillRiderUsingUPI() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                ADD_MONEY R1 520
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                PAY RIDE-001 UPI
                ADMIN_VIEW_DRIVER_EARNINGS D3
                """;

        String expectedOutput = """
                CURRENT_BALANCE R1 520.0
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                PAID D3 186.7 VIA UPI
                DRIVER_EARNINGS D3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void SwitchBetweenDatabases() {
        String input = """
                CONNECT_MRD
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                CONNECT_IMDB
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                """;

        String expectedOutput = """
                CONNECTED TO MOCK DATABASE
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                CONNECTED TO IN-MEMORY DATABASE
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void NoDriversAvailableAtAll_ThrowsException() {
        String input = """
                ADD_RIDER R1 3 5
                ADD_RIDER R2 1 1
                MATCH R1
                MATCH R2
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
                ADD_DRIVER D1 0 1
                ADD_DRIVER D2 2 3
                ADD_RIDER R1 3 5
                ADD_DRIVER D3 4 2
                ADD_RIDER R2 1 1
                MATCH R1
                MATCH R2
                START_RIDE RIDE-101 1 R1
                START_RIDE RIDE-101 1 R2
                STOP_RIDE RIDE-101 10 2 48
                BILL RIDE-101
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D2 D3 D1
                DRIVERS_MATCHED D1 D2 D3
                RIDE_STARTED RIDE-101
                INVALID_RIDE
                RIDE_STOPPED RIDE-101
                BILL RIDE-101 D2 234.6
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StartRideWithBusyDriver_ThrowsException() {
        String input = """
                ADD_DRIVER D1 0 1
                ADD_DRIVER D2 2 3
                ADD_RIDER R1 3 5
                ADD_DRIVER D3 4 2
                ADD_RIDER R2 1 1
                MATCH R1
                MATCH R2
                START_RIDE RIDE-101 1 R1
                START_RIDE RIDE-102 2 R2
                STOP_RIDE RIDE-101 10 2 48
                BILL RIDE-101
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D2 D3 D1
                DRIVERS_MATCHED D1 D2 D3
                RIDE_STARTED RIDE-101
                INVALID_RIDE
                RIDE_STOPPED RIDE-101
                BILL RIDE-101 D2 234.6
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StartRideWithNonExistentDriver_ThrowsException() {
        String input = """
                ADD_DRIVER D1 0 1
                ADD_DRIVER D2 2 3
                ADD_RIDER R1 3 5
                ADD_DRIVER D3 4 2
                ADD_RIDER R2 1 1
                MATCH R1
                MATCH R2
                START_RIDE RIDE-101 1 R1
                START_RIDE RIDE-102 4 R2
                STOP_RIDE RIDE-101 10 2 48
                BILL RIDE-101
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D2 D3 D1
                DRIVERS_MATCHED D1 D2 D3
                RIDE_STARTED RIDE-101
                INVALID_RIDE
                RIDE_STOPPED RIDE-101
                BILL RIDE-101 D2 234.6
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StopAlreadyCompletedRide_ThrowsException() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                STOP_RIDE RIDE-001 1 1 10
                BILL RIDE-001
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                INVALID_RIDE
                BILL RIDE-001 D3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void StopNonExistentRide_ThrowsException() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                STOP_RIDE RIDE-999 1 1 10
                BILL RIDE-001
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                INVALID_RIDE
                BILL RIDE-001 D3 186.7
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void TestAdminCommands() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                RATE_DRIVER D3 4.5
                ADMIN_REMOVE_DRIVER D2
                ADMIN_LIST_DRIVERS 2
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                CURRENT_RATING D3 4.5
                REMOVED_DRIVER D2
                DRIVER_D1 (X=1, Y=1) RATING 0.0
                DRIVER_D3 (X=2, Y=2) RATING 4.5
                """;

        runTest(input, expectedOutput);
    }

    @Test
    void DeleteNonExistentDriver_ThrowsException() {
        String input = """
                ADD_DRIVER D1 1 1
                ADD_DRIVER D2 4 5
                ADD_DRIVER D3 2 2
                ADD_RIDER R1 0 0
                MATCH R1
                START_RIDE RIDE-001 2 R1
                STOP_RIDE RIDE-001 4 5 32
                BILL RIDE-001
                ADMIN_REMOVE_DRIVER D99
                """;

        String expectedOutput = """
                DRIVERS_MATCHED D1 D3
                RIDE_STARTED RIDE-001
                RIDE_STOPPED RIDE-001
                BILL RIDE-001 D3 186.7
                INVALID_DRIVER_ID
                """;

        runTest(input, expectedOutput);
    }


    private void runTest(String input, String expectedOutput) {
        ByteArrayInputStream testInput = new ByteArrayInputStream(input.getBytes());
        System.setIn(testInput);

        RiderApp.reset();
        RiderApp.main(new String[]{});

        Assertions.assertEquals(expectedOutput, testOutput.toString());
    }
}
