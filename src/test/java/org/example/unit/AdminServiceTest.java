package org.example.unit;


import org.example.config.TestConfig;
import org.example.dto.DriverDTO;
import org.example.repository.Database;
import org.example.models.Driver;
import org.junit.jupiter.api.Test;
import org.example.services.admin.AdminService;
import org.example.exceptions.InvalidDriverIDException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class AdminServiceTest {
    @Autowired
    private Database mockDB;

    @Autowired
    @InjectMocks
    private AdminService adminService;

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

        List<DriverDTO> output = adminService.listNDriverDetails(N);

        // Expected output list
        List<DriverDTO> expected = List.of(
                new DriverDTO("D1", 5, 5, 0f),
                new DriverDTO("D2", 2, 7, 0f),
                new DriverDTO("D3", 9, 3, 0f)
        );

        assertEquals(expected.size(), output.size(), "List size should match");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), output.get(i), "Driver details should match");
        }
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


        assertEquals("INVALID_DRIVER_ID", exception.getMessage(), "There is no driver that can be deleted");
    }
}