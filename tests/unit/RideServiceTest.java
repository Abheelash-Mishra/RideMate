package unit;

import database.Database;
import models.Driver;
import models.Ride;
import models.Rider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.ride.RideService;
import services.ride.RideServiceInterface;
import services.ride.exceptions.InvalidRideException;
import services.ride.impl.RideServiceConsoleImpl;
import utils.TestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RideServiceTest {
    private Database mockDB;
    private RideService rideService;

    @BeforeEach
    void setUp() {
        mockDB = mock(Database.class);

        RideServiceInterface rideServiceImpl = new RideServiceConsoleImpl(mockDB);
        rideService = new RideService(rideServiceImpl);

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

        TestUtils.captureOutput();
        rideService.matchRider("R1");
        String output = TestUtils.getCapturedOutput();

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

        TestUtils.captureOutput();
        rideService.startRide("RIDE-001", N, riderID);
        String output = TestUtils.getCapturedOutput();

        assertTrue(output.contains("RIDE_STARTED RIDE-001"), "Ride did not start");
    }

    @Test
    void stopRide() {
        Ride ride = new Ride("R1", "D3");
        mockDB.getRideDetails().put("RIDE-001", ride);
        mockDB.getDriverDetails().get("D3").updateAvailability();

        TestUtils.captureOutput();
        rideService.stopRide("RIDE-001", 4, 5, 32);
        String output = TestUtils.getCapturedOutput();

        assertTrue(output.contains("RIDE_STOPPED RIDE-001"), "Ride did not stop");
    }

    @Test
    void billRide() {
        String riderID = "R1";
        mockDB.getRiderDetails().put(riderID, new Rider(0, 0));

        Ride ride = new Ride("R1", "D3");
        ride.finishRide(4, 5, 32);
        mockDB.getRideDetails().put("RIDE-001", ride);

        TestUtils.captureOutput();
        rideService.billRide("RIDE-001");
        String output = TestUtils.getCapturedOutput();

        assertTrue(output.contains("BILL RIDE-001 D3 186.7"), "Bill was not generated correctly");
    }

    @Test
    void matchRiderWhenNoDriversAvailable_ThrowsException() {
        String riderID = "R1";
        mockDB.getRiderDetails().put(riderID, new Rider(10, 10));

        TestUtils.captureOutput();
        rideService.matchRider("R1");
        String output = TestUtils.getCapturedOutput();

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