package org.example.unit;


import org.example.database.Database;
import org.example.models.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.services.admin.AdminService;
import org.example.exceptions.InvalidDriverIDException;
import org.example.services.admin.AdminServiceImpl;
import org.example.utils.TestUtils;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminServiceTest {
    private Database mockDB;
    private AdminService adminService;

    @BeforeEach
    void setup() {
        mockDB = mock(Database.class);

        AdminService adminServiceImpl = new AdminServiceImpl(mockDB);
        adminService = new AdminService(adminServiceImpl);
    }

    @Test
    void removeDriver() {
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

        TestUtils.captureOutput();
        adminService.listNDriverDetails(N);
        String output = TestUtils.getCapturedOutput();

        assertTrue(output.contains("DRIVER_D1 (X=5, Y=5)"), "D1 should be in output");
        assertTrue(output.contains("DRIVER_D2 (X=2, Y=7)"), "D2 should be in output");
        assertTrue(output.contains("DRIVER_D3 (X=9, Y=3)"), "D3 should be in output");
    }

    @Test
    void removeNonExistentDriver_ThrowsException() {
        String driverID = "D2";
        HashMap<String, Driver> drivers = new HashMap<>();

        drivers.put("D1", new Driver(5, 5));
        drivers.put("D3", new Driver(9, 3));

        when(mockDB.getDriverDetails()).thenReturn(drivers);

        Exception exception = assertThrows(InvalidDriverIDException.class, () -> {
            adminService.removeDriver(driverID);
        });


        assertEquals("INVALID_DRIVER_ID", exception.getMessage(), "Ride was not supposed to start");
    }
}