package org.example.unit;

import org.example.dto.DriverDTO;
import org.example.dto.DriverEarningsDTO;
import org.example.models.Driver;
import org.example.repository.DriverRepository;
import org.example.services.AdminService;
import org.junit.jupiter.api.Test;
import org.example.exceptions.InvalidDriverIDException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @MockitoBean
    private DriverRepository driverRepository;

    @Autowired
    private AdminService adminService;

    @Test
    void removeDriver() {
        long driverID = 2;

        when(driverRepository.existsById(driverID)).thenReturn(true);
        doNothing().when(driverRepository).deleteById(driverID);

        adminService.removeDriver(driverID);

        verify(driverRepository, times(1)).deleteById(driverID);
    }

    @Test
    void listNDriverDetails() {
        int N = 4;

        Driver d1 = new Driver("9876556789", "Main Street", 5, 5);
        d1.setId(1L);
        Driver d2 = new Driver("9876556789", "Main Street", 2, 7);
        d2.setId(2L);
        Driver d3 = new Driver("9876556789", "Main Street", 9, 3);
        d3.setId(3L);

        List<Driver> drivers = List.of(d1, d2, d3);
        when(driverRepository.findFirstNDrivers(N)).thenReturn(drivers);

        List<DriverDTO> output = adminService.listNDriverDetails(N);

        List<DriverDTO> expected = List.of(
                new DriverDTO(1, 5, 5, 0f),
                new DriverDTO(2, 2, 7, 0f),
                new DriverDTO(3, 9, 3, 0f)
        );

        assertEquals(expected, output, "Mismatch in driver details returned");
    }

    @Test
    void getDriverEarnings() {
        long driverID = 1;
        Driver driver = new Driver("9876556789", "Main Street", 5, 5);
        driver.setEarnings(150.0f);

        when(driverRepository.findById(driverID)).thenReturn(Optional.of(driver));

        DriverEarningsDTO result = adminService.getDriverEarnings(driverID);

        assertEquals(driverID, result.getDriverID(), "Driver ID does not match");
        assertEquals(150.0f, result.getEarnings(), "Driver's earnings do not match");
    }


    @Test
    void removeNonExistentDriver_ThrowsException() {
        long driverID = 2;

        when(driverRepository.existsById(driverID)).thenReturn(false);

        Exception exception = assertThrows(InvalidDriverIDException.class, () -> adminService.removeDriver(driverID));

        assertEquals("Invalid Driver ID - 2, no such driver exists", exception.getMessage(), "Invalid driver ID exception should be thrown");
    }
}