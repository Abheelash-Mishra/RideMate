package org.example.unit;

import org.example.config.TestConfig;
import org.example.repository.Database;
import org.example.models.Driver;
import org.junit.jupiter.api.Test;
import org.example.exceptions.InvalidDriverIDException;
import org.example.services.driver.DriverService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class DriverServiceTest {
    @Autowired
    private Database mockDB;

    @Autowired
    @InjectMocks
    private DriverService driverService;

    @Test
    void addDriver() {
        HashMap<String, Driver> drivers = new HashMap<>();
        when(mockDB.getDriverDetails()).thenReturn(drivers);

        driverService.addDriver("D1", 5, 8);

        assertTrue(mockDB.getDriverDetails().containsKey("D1"), "D1 is not present");
    }

    @Test
    void rateDriver() throws InvalidDriverIDException {
        HashMap<String, Driver> drivers = new HashMap<>();
        when(mockDB.getDriverDetails()).thenReturn(drivers);

        String driverID = "D1";
        Driver driver = new Driver(5, 8);
        drivers.put(driverID, driver);

        driverService.rateDriver("D1", 4.9F);

        assertEquals(
                4.9F,
                mockDB.getDriverDetails().get(driverID).getRating(),
                0.1,
                "Driver rating should be updated correctly"
        );
    }
}