package services.admin;


import database.Database;
import models.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.admin.exceptions.InvalidDriverIDException;
import services.admin.impl.AdminServiceConsoleImpl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminServiceTest {
    private Database mockDB;
    private AdminService adminService;

    @BeforeEach
    void setup() {
        mockDB = mock(Database.class);

        AdminServiceInterface adminServiceImpl = new AdminServiceConsoleImpl(mockDB);
        adminService = new AdminService(adminServiceImpl);
    }

    @Test
    void removeDriver() throws InvalidDriverIDException {
        String driverID = "D2";
        HashMap<String, Driver> drivers = new HashMap<>();

        drivers.put("D1", new Driver(5, 5));
        drivers.put("D2", new Driver(2, 7));
        drivers.put("D3", new Driver(9, 3));

        when(mockDB.getDriverDetails()).thenReturn(drivers);

        adminService.removeDriver(driverID);

        assertFalse(mockDB.getDriverDetails().containsKey(driverID));
    }

    @Test
    void listNDriverDetails() {
        int N = 4;
        HashMap<String, Driver> drivers = new HashMap<>();

        drivers.put("D1", new Driver(5, 5));
        drivers.put("D2", new Driver(2, 7));
        drivers.put("D3", new Driver(9, 3));

        when(mockDB.getDriverDetails()).thenReturn(drivers);

        // Captures output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        adminService.listNDriverDetails(N);

        System.setOut(System.out);      // Restore sysout
        String output = outputStream.toString();

        assertTrue(output.contains("DRIVER_D1 (X=5, Y=5)"), "D1 should be in output");
        assertTrue(output.contains("DRIVER_D2 (X=2, Y=7)"), "D2 should be in output");
        assertTrue(output.contains("DRIVER_D3 (X=9, Y=3)"), "D3 should be in output");
    }
}