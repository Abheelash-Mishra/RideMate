package org.example.unit;

import org.example.dto.DriverRatingDTO;
import org.example.models.Driver;
import org.example.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.example.exceptions.InvalidDriverIDException;
import org.example.services.DriverService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class DriverServiceTest {
    @MockitoBean
    private DriverRepository driverRepository;

    @Autowired
    private DriverService driverService;

    @Test
    void addDriver() {
        Driver driver = new Driver(1, 5, 8);

        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        driverService.addDriver(1, 5, 8);

        verify(driverRepository, times(1)).save(any(Driver.class));
    }


    @Test
    void rateDriver() throws InvalidDriverIDException {
        long driverID = 1;
        Driver driver = new Driver(driverID, 5, 8);

        when(driverRepository.findById(driverID)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        DriverRatingDTO expected = new DriverRatingDTO(driverID, 4.6f);

        float newRating = 4.6f;
        DriverRatingDTO actual = driverService.rateDriver(driverID, newRating);

        assertEquals(expected, actual, "Driver's rating don't match");
    }
}