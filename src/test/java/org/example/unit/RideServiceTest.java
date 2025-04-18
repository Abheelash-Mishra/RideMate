package org.example.unit;

import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideDetailsDTO;
import org.example.dto.RideStatusDTO;
import org.example.exceptions.InvalidDriverIDException;
import org.example.exceptions.InvalidRideException;
import org.example.models.RideStatus;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.models.Rider;

import org.example.repository.DriverRepository;
import org.example.repository.RideRepository;
import org.example.repository.RiderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.example.services.RideService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RideServiceTest {
    @MockitoBean
    private RiderRepository riderRepository;

    @MockitoBean
    private DriverRepository driverRepository;

    @MockitoBean
    private RideRepository rideRepository;

    @Autowired
    private RideService rideService;

    @Test
    void addRider() {
        Rider rider = new Rider("r1@email.com", "9876556789", 0, 0);

        when(riderRepository.save(any(Rider.class))).thenReturn(rider);

        rideService.addRider("r1@email.com", "9876556789", 0, 0);

        verify(riderRepository).save(any(Rider.class));
    }

    @Test
    void matchRider() {
        long riderID = 1;
        Rider rider = new Rider("r1@email.com", "9876556789", 0, 0);
        rider.setRiderID(riderID);

        Driver d1 = new Driver("d1@email.com", "9876556789", 1, 1);
        d1.setDriverID(1);
        Driver d3 = new Driver("d3@email.com", "9876556789", 2, 2);
        d3.setDriverID(3);

        List<Driver> drivers = List.of(d1, d3);

        when(riderRepository.findById(riderID)).thenReturn(Optional.of(rider));
        when(driverRepository.findAll()).thenReturn(drivers);

        MatchedDriversDTO response = rideService.matchRider(riderID);

        List<Long> expected = List.of(1L, 3L);
        assertEquals(expected, response.getMatchedDrivers(), "Drivers are not matched correctly");
    }

    @Test
    void startRide() {
        long riderID = 1;
        Rider rider = new Rider("r1@email.com", "9876556789", 0, 0);
        rider.setMatchedDrivers(List.of(1L, 3L));

        long driverID = 3;
        Driver driver = new Driver("d3@email.com", "9876556789", 2, 2);

        Ride ride = new Ride(rider, driver);

        when(riderRepository.findById(riderID)).thenReturn(Optional.of(rider));
        when(driverRepository.findById(driverID)).thenReturn(Optional.of(driver));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        RideStatusDTO expected = new RideStatusDTO(0, 1, 3, RideStatus.ONGOING);
        RideStatusDTO actual = rideService.startRide(2, riderID, "Beach", 10, 10);

        assertEquals(expected, actual, "Ride was not started correctly");
    }

    @Test
    void stopRide() {
        long rideID = 1;

        long riderID = 1;
        Rider rider = new Rider("r1@email.com", "9876556789", 0, 0);
        rider.setRiderID(riderID);
        rider.setMatchedDrivers(List.of(1L, 3L));

        long driverID = 3;
        Driver driver = new Driver("d3@email.com", "9876556789", 2, 2);
        driver.setDriverID(driverID);

        Ride ride = new Ride(rider, driver);
        ride.setStatus(RideStatus.ONGOING);

        when(rideRepository.findById(rideID)).thenReturn(Optional.of(ride));
        when(driverRepository.findById(driverID)).thenReturn(Optional.of(driver));

        RideStatusDTO expected = new RideStatusDTO(1, 1, 3, RideStatus.FINISHED);
        RideStatusDTO actual = rideService.stopRide(rideID, 32);

        assertEquals(expected, actual, "Ride status should be FINISHED");
    }

    @Test
    void billRide() {
        long rideID = 1;

        Rider rider = new Rider("r1@email.com", "9876556789", 0, 0);
        rider.setMatchedDrivers(List.of(1L, 3L));

        Driver driver = new Driver("d3@email.com", "9876556789", 2, 2);

        Ride ride = new Ride(rider, driver);
        ride.setDestinationCoordinates(List.of(4, 5));
        ride.setTimeTakenInMins(32);
        ride.setStatus(RideStatus.FINISHED);

        when(rideRepository.findById(rideID)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        double bill = rideService.billRide(rideID);

        assertEquals(186.7, bill, 0.1);
    }

    @Test
    void getAllRidesOfARider() {
        long riderId = 1L;

        Object[] row1 = new Object[]{1L, 2L, "City A", 230.5f, 2};
        Object[] row2 = new Object[]{2L, 3L, "City B", 450.0f, 1};

        when(rideRepository.findAllRides(riderId)).thenReturn(Arrays.asList(row1, row2));

        List<RideDetailsDTO> expected = List.of(
                new RideDetailsDTO(1L, 2L, "City A", 230.5f, 2),
                new RideDetailsDTO(2L, 3L, "City B", 450.0f, 1)
        );
        List<RideDetailsDTO> result = rideService.getAllRides(riderId);

        assertEquals(2, result.size());
        assertEquals(expected, result);
    }

    @Test
    void matchRiderWhenNoDriversAvailable() {
        long riderID = 1;
        Rider rider = new Rider("r1@email.com", "9876556789", 0, 0);

        List<Driver> drivers = List.of(
                new Driver("d1@email.com", "9876556789", 7, 1),
                new Driver("d2@email.com", "9876556789", 7, 2)
        );

        when(riderRepository.findById(riderID)).thenReturn(Optional.of(rider));
        when(driverRepository.findAll()).thenReturn(drivers);

        MatchedDriversDTO response = rideService.matchRider(riderID);

        List<Long> expected = Collections.emptyList();
        assertEquals(expected, response.getMatchedDrivers(), "The list should be empty as there are no available drivers");
    }

    @Test
    void startRideWithNonExistentDriver_ThrowsException() {
        long riderID = 1;
        Rider rider = new Rider("r1@email.com", "9876556789", 0, 0);
        rider.setMatchedDrivers(List.of(1L, 3L));

        long driverID = 2;
        Driver driver = new Driver("d2@email.com", "9876556789", 2, 2);

        Ride ride = new Ride(rider, driver);

        when(riderRepository.findById(riderID)).thenReturn(Optional.of(rider));
        when(driverRepository.findById(driverID)).thenReturn(Optional.of(driver));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        Exception exception = Assertions.assertThrows(InvalidDriverIDException.class, () -> rideService.startRide(2, riderID, "Beach", 10, 10));

        assertEquals("Invalid Driver ID - 3, no such driver exists", exception.getMessage(), "Ride should not be started with this driver");
    }

    @Test
    void stopInvalidRide_ThrowsException() {
        Exception exception = Assertions.assertThrows(InvalidRideException.class, () -> rideService.stopRide(1, 32));

        assertEquals("Invalid Ride ID - 1, no such ride exists", exception.getMessage(), "There is no ride that can be stopped");
    }

    @Test
    void billUnfinishedRide_ThrowsException() {
        long rideID = 1;

        Rider rider = new Rider("r1@email.com", "9876556789", 0, 0);
        rider.setMatchedDrivers(List.of(1L, 3L));

        Driver driver = new Driver("d3@email.com", "9876556789", 2, 2);

        Ride ride = new Ride(rider, driver);
        ride.setDestinationCoordinates(List.of(4, 5));
        ride.setTimeTakenInMins(32);

        when(rideRepository.findById(rideID)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        Exception exception = Assertions.assertThrows(InvalidRideException.class, () -> rideService.billRide(1));

        assertEquals("Invalid Ride ID - 1", exception.getMessage(), "Unfinished ride should not be billed");
    }
}