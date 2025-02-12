import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import services.RideService;

import java.io.*;

import static org.junit.Assert.assertEquals;

class RiderAppTests {
    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayInputStream testInput;
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
    void Testcase1() {
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
    void Testcase2() {
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


    private void runTest(String input, String expectedOutput) {
        testInput = new ByteArrayInputStream(input.getBytes());
        System.setIn(testInput);

        RiderApp.reset();
        RiderApp.main(new String[]{});

        Assertions.assertEquals(expectedOutput, testOutput.toString());
    }
}
