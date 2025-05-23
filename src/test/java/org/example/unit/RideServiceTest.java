package org.example.unit;

import org.example.config.TestConfig;
import org.example.dto.DriverRatingDTO;
import org.example.dto.MatchedDriversDTO;
import org.example.dto.RideDetailsDTO;
import org.example.dto.RideStatusDTO;
import org.example.exceptions.InvalidDriverIDException;
import org.example.exceptions.InvalidRideException;
import org.example.models.*;

import org.example.repository.DriverRepository;
import org.example.repository.RideRepository;
import org.example.repository.RiderRepository;
import org.example.repository.UserRepository;
import org.example.services.impl.RideServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.services.RideService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestConfig.class)
@ExtendWith(MockitoExtension.class)
class RideServiceTest {
    @MockitoBean
    private RiderRepository riderRepository;

    @MockitoBean
    private DriverRepository driverRepository;

    @MockitoBean
    private RideRepository rideRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private RideService rideService;

    @Spy
    @InjectMocks
    private RideServiceImpl rideServiceImpl;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @BeforeEach
    void setUp() {
        String mockEmail = "rider@example.com";
        Authentication auth = new UsernamePasswordAuthenticationToken(mockEmail, null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void matchRider() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("rider@example.com");
        mockUser.setPassword("test@abc");
        mockUser.setRole(Role.RIDER);

        long riderID = 1L;
        Rider rider = new Rider("9876556789", "Main Street", 0, 0);
        rider.setId(riderID);
        rider.setUser(mockUser);

        Driver d1 = new Driver("9876556789", "Main Street", 1, 1);
        d1.setId(1);
        Driver d3 = new Driver("9876556789", "Main Street", 2, 2);
        d3.setId(3);

        List<Long> driverIDs = List.of(1L, 3L);

        doReturn(1L).when(rideServiceImpl).getUserId();
        when(userRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(mockUser));
        when(riderRepository.findByUserId(1L)).thenReturn(Optional.of(rider));
        when(driverRepository.findNearbyDrivers(rider.getX_coordinate(), rider.getY_coordinate(), 5.0, PageRequest.of(0, 20))).thenReturn(driverIDs);

        MatchedDriversDTO response = rideService.matchRider();

        List<Long> expected = List.of(1L, 3L);
        assertEquals(expected, response.getMatchedDrivers(), "Drivers are not matched correctly");
    }

    @Test
    void startRide() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("rider@example.com");
        mockUser.setPassword("test@abc");
        mockUser.setRole(Role.RIDER);

        long riderID = 1L;
        Rider rider = new Rider("9876556789", "Main Street", 0, 0);
        rider.setId(riderID);
        rider.setUser(mockUser);
        rider.setMatchedDrivers(List.of(1L, 3L));

        long driverID = 3L;
        Driver driver = new Driver("9876556789", "Main Street", 2, 2);

        Ride ride = new Ride(rider, driver);

        doReturn(1L).when(rideServiceImpl).getUserId();
        when(userRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(mockUser));
        when(riderRepository.findByUserId(1L)).thenReturn(Optional.of(rider));
        when(driverRepository.findById(driverID)).thenReturn(Optional.of(driver));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        RideStatusDTO expected = new RideStatusDTO(0, 1, 3, RideStatus.ONGOING);
        RideStatusDTO actual = rideService.startRide(2, "Beach", 10, 10);

        assertEquals(expected, actual, "Ride was not started correctly");
    }

    @Test
    void stopRide() {
        long rideID = 1;

        long riderID = 1L;
        Rider rider = new Rider("9876556789", "Main Street", 0, 0);
        rider.setId(riderID);
        rider.setMatchedDrivers(List.of(1L, 3L));

        long driverID = 3L;
        Driver driver = new Driver("9876556789", "Main Street", 2, 2);
        driver.setId(driverID);

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

        Rider rider = new Rider("9876556789", "Main Street", 0, 0);
        rider.setMatchedDrivers(List.of(1L, 3L));

        Driver driver = new Driver("9876556789", "Main Street", 2, 2);

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
    void rateDriver() throws InvalidDriverIDException {
        long driverID = 1;
        Driver driver = new Driver("9876556789", "Main Street", 5, 8);

        when(driverRepository.findById(driverID)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        // Rider is null for the ride because it does not matter in this unit test
        when(rideRepository.findById(1L)).thenReturn(Optional.of(new Ride(null, driver)));
        when(rideRepository.save(any(Ride.class))).thenReturn(new Ride(null, driver));

        DriverRatingDTO expected = new DriverRatingDTO(driverID, 4.6f);

        float newRating = 4.6f;
        DriverRatingDTO actual = rideService.rateDriver(1, driverID, newRating, "Drives great");

        assertEquals(expected, actual, "Driver's rating don't match");
    }

    @Test
    void getAllRidesOfARider() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("rider@example.com");
        mockUser.setPassword("test@abc");
        mockUser.setRole(Role.RIDER);

        long riderID = 1L;
        Rider rider = new Rider("9876556789", "Main Street", 0, 0);
        rider.setId(riderID);
        rider.setUser(mockUser);

        Object[] row1 = new Object[]{1L, 2L, "City A", 230.5f, 21, PaymentMethodType.CASH.toString()};
        Object[] row2 = new Object[]{2L, 3L, "City B", 450.0f, 51, PaymentMethodType.CASH.toString()};

        doReturn(1L).when(rideServiceImpl).getUserId();
        when(userRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(mockUser));
        when(riderRepository.findByUserId(1L)).thenReturn(Optional.of(rider));
        when(rideRepository.findAllRides(1)).thenReturn(Arrays.asList(row1, row2));

        List<RideDetailsDTO> expected = List.of(
                new RideDetailsDTO(1L, 2L, "City A", 230.5f, 21, PaymentMethodType.CASH.toString()),
                new RideDetailsDTO(2L, 3L, "City B", 450.0f, 51, PaymentMethodType.CASH.toString())
        );
        List<RideDetailsDTO> result = rideService.getAllRides();

        assertEquals(2, result.size());
        assertEquals(expected, result);
    }

    @Test
    void getAllRidesOfANewRider() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("rider@example.com");
        mockUser.setPassword("test@abc");
        mockUser.setRole(Role.RIDER);

        long riderID = 1L;
        Rider rider = new Rider("9876556789", "Main Street", 0, 0);
        rider.setId(riderID);
        rider.setUser(mockUser);

        doReturn(1L).when(rideServiceImpl).getUserId();
        when(userRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(mockUser));
        when(riderRepository.findByUserId(1L)).thenReturn(Optional.of(rider));
        when(rideRepository.findAllRides(1L)).thenReturn(Collections.emptyList());

        List<RideDetailsDTO> expected = Collections.emptyList();
        List<RideDetailsDTO> result = rideService.getAllRides();

        assertEquals(0, result.size());
        assertEquals(expected, result);
    }

    @Test
    void matchRiderWhenNoDriversAvailable() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("rider@example.com");
        mockUser.setPassword("test@abc");
        mockUser.setRole(Role.RIDER);

        long riderID = 1L;
        Rider rider = new Rider("9876556789", "Main Street", 0, 0);
        rider.setId(riderID);
        rider.setUser(mockUser);

        List<Driver> drivers = List.of(
                new Driver("9876556789", "Main Street", 7, 1),
                new Driver("9876556789", "Main Street", 7, 2)
        );

        doReturn(1L).when(rideServiceImpl).getUserId();
        when(userRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(mockUser));
        when(riderRepository.findByUserId(1L)).thenReturn(Optional.of(rider));
        when(driverRepository.findAll()).thenReturn(drivers);

        MatchedDriversDTO response = rideService.matchRider();

        List<Long> expected = Collections.emptyList();
        assertEquals(expected, response.getMatchedDrivers(), "The list should be empty as there are no available drivers");
    }

    @Test
    void startRideWithNonExistentDriver_ThrowsException() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("rider@example.com");
        mockUser.setPassword("test@abc");
        mockUser.setRole(Role.RIDER);

        long riderID = 1L;
        Rider rider = new Rider("9876556789", "Main Street", 0, 0);
        rider.setId(riderID);
        rider.setUser(mockUser);
        rider.setMatchedDrivers(List.of(1L, 3L));

        long driverID = 2;
        Driver driver = new Driver("9876556789", "Main Street", 2, 2);

        Ride ride = new Ride(rider, driver);

        doReturn(1L).when(rideServiceImpl).getUserId();
        when(userRepository.findByEmail("rider@example.com")).thenReturn(Optional.of(mockUser));
        when(riderRepository.findByUserId(1L)).thenReturn(Optional.of(rider));
        when(driverRepository.findById(driverID)).thenReturn(Optional.of(driver));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        Exception exception = Assertions.assertThrows(InvalidDriverIDException.class, () -> rideService.startRide(2, "Beach", 10, 10));

        assertEquals("Invalid Driver ID - 3", exception.getMessage(), "Ride should not be started with this driver");
    }

    @Test
    void stopInvalidRide_ThrowsException() {
        Exception exception = Assertions.assertThrows(InvalidRideException.class, () -> rideService.stopRide(1, 32));

        assertEquals("Invalid Ride ID - 1, no such ride exists", exception.getMessage(), "There is no ride that can be stopped");
    }

    @Test
    void billUnfinishedRide_ThrowsException() {
        long rideID = 1;

        Rider rider = new Rider("9876556789", "Main Street", 0, 0);
        rider.setMatchedDrivers(List.of(1L, 3L));

        Driver driver = new Driver("9876556789", "Main Street", 2, 2);

        Ride ride = new Ride(rider, driver);
        ride.setDestinationCoordinates(List.of(4, 5));
        ride.setTimeTakenInMins(32);

        when(rideRepository.findById(rideID)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);

        Exception exception = Assertions.assertThrows(InvalidRideException.class, () -> rideService.billRide(1));

        assertEquals("Invalid Ride ID - 1", exception.getMessage(), "Unfinished ride should not be billed");
    }
}