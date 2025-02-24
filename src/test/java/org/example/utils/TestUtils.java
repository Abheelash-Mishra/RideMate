package org.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestUtils {
    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public static void captureOutput() {
        System.setOut(new PrintStream(outputStream));
    }

    public static String getCapturedOutput() {
        System.setOut(originalOut);             // Restore System.out
        return outputStream.toString().trim();  // Trim to remove extra newlines
    }
}
