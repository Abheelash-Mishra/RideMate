package org.example.unit;

import org.example.config.TestConfig;
import org.example.repository.Database;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.models.Rider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.example.services.ride.RideService;
import org.example.exceptions.InvalidRideException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class RideServiceTest {
    @Autowired
    private Database mockDB;

    @Autowired
    @InjectMocks
    private RideService rideService;

    @BeforeEach
    void setUp() {
        HashMap<String, Rider> riderDetails = new HashMap<>();
        HashMap<String, Driver> driverDetails = new HashMap<>();
        HashMap<String, Ride> rideDetails = new HashMap<>();
        HashMap<String, List<String>> riderDriverMapping = new HashMap<>();

        when(mockDB.getRiderDetails()).thenReturn(riderDetails);
        when(mockDB.getDriverDetails()).thenReturn(driverDetails);
        when(mockDB.getRideDetails()).thenReturn(rideDetails);
        when(mockDB.getRiderDriverMapping()).thenReturn(riderDriverMapping);

        driverDetails.put("D1", new Driver(1, 1));
        driverDetails.put("D2", new Driver(4, 5));
        driverDetails.put("D3", new Driver(2, 2));
    }

    @Test
    void addRider() {
        String riderID = "R1";
        rideService.addRider(riderID, 0, 0);

        assertTrue(mockDB.getRiderDetails().containsKey(riderID), "DB does not have rider ID");
    }

    @Test
    void matchRider() {
        String riderID = "R1";
        mockDB.getRiderDetails().put(riderID, new Rider(0, 0));

        String output = rideService.matchRider("R1");

        assertTrue(output.contains("DRIVERS_MATCHED D1 D3"), "Wrong drivers were matched");
    }

    @Test
    void startRide() {
        String riderID = "R1";
        int N = 2;
        ArrayList<String> matchedDrivers = new ArrayList<>();
        matchedDrivers.add("D1");
        matchedDrivers.add("D3");

        mockDB.getRiderDriverMapping().put(riderID, matchedDrivers);

        String output = rideService.startRide("RIDE-001", N, riderID);

        assertTrue(output.contains("RIDE_STARTED RIDE-001"), "Ride did not start");
    }

    @Test
    void stopRide() {
        Ride ride = new Ride("R1", "D3");
        mockDB.getRideDetails().put("RIDE-001", ride);
        mockDB.getDriverDetails().get("D3").updateAvailability();

        String output = rideService.stopRide("RIDE-001", 4, 5, 32);

        assertTrue(output.contains("RIDE_STOPPED RIDE-001"), "Ride did not stop");
    }

    @Test
    void billRide() {
        String riderID = "R1";
        mockDB.getRiderDetails().put(riderID, new Rider(0, 0));

        Ride ride = new Ride("R1", "D3");
        ride.finishRide(4, 5, 32);
        mockDB.getRideDetails().put("RIDE-001", ride);

        double bill = rideService.billRide("RIDE-001");

        assertEquals(186.7, bill, 0.1);
    }

    @Test
    void matchRiderWhenNoDriversAvailable_ThrowsException() {
        String riderID = "R1";
        mockDB.getRiderDetails().put(riderID, new Rider(10, 10));

        String output = rideService.matchRider("R1");

        assertTrue(output.contains("NO_DRIVERS_AVAILABLE"), "Drivers were still matched");
    }

    @Test
    void startRideWithNonExistentDriver_ThrowsException() {
        String riderID = "R1";
        int N = 5;
        ArrayList<String> matchedDrivers = new ArrayList<>();
        matchedDrivers.add("D1");
        matchedDrivers.add("D3");

        mockDB.getRiderDriverMapping().put(riderID, matchedDrivers);

        Exception exception = assertThrows(InvalidRideException.class, () -> {
            rideService.startRide("RIDE-001", N, riderID);
        });


        assertEquals("INVALID_RIDE", exception.getMessage(), "Ride was not supposed to start");
    }

    @Test
    void stopInvalidRide_ThrowsException() {
        Exception exception = assertThrows(InvalidRideException.class, () -> {
            rideService.stopRide("RIDE-001", 4, 5, 32);
        });

        assertEquals("INVALID_RIDE", exception.getMessage(), "There is no ride that can be stopped");
    }

    @Test
    void billUnfinishedRide_ThrowsException() {
        String riderID = "R1";
        mockDB.getRiderDetails().put(riderID, new Rider(0, 0));

        Ride ride = new Ride("R1", "D3");
        mockDB.getRideDetails().put("RIDE-001", ride);

        Exception exception = assertThrows(InvalidRideException.class, () -> {
            rideService.billRide("RIDE-001");
        });

        assertEquals("INVALID_RIDE", exception.getMessage(), "Unfinished ride should not be billed");
    }
}