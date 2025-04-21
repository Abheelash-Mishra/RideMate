package org.example.unit;

import org.example.dto.DriverRatingDTO;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.repository.DriverRepository;
import org.example.repository.RideRepository;
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

    @MockitoBean
    private RideRepository rideRepository;

    @Autowired
    private DriverService driverService;

    @Test
    void addDriver() {
        Driver driver = new Driver("d1@email.com", "9876556789", 5, 8);

        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        driverService.addDriver("d1@email.com", "9876556789", 5, 8);

        verify(driverRepository, times(1)).save(any(Driver.class));
    }


    @Test
    void rateDriver() throws InvalidDriverIDException {
        long driverID = 1;
        Driver driver = new Driver("d1@email.com", "9876556789", 5, 8);

        when(driverRepository.findById(driverID)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        // Rider is null for the ride because it does not matter in this unit test
        when(rideRepository.findById(1L)).thenReturn(Optional.of(new Ride(null, driver)));
        when(rideRepository.save(any(Ride.class))).thenReturn(new Ride(null, driver));

        DriverRatingDTO expected = new DriverRatingDTO(driverID, 4.6f);

        float newRating = 4.6f;
        DriverRatingDTO actual = driverService.rateDriver(1, driverID, newRating, "Drives great");

        assertEquals(expected, actual, "Driver's rating don't match");
    }
}