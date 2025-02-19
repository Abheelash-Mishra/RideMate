package database;

import models.Driver;
import models.Ride;
import models.Rider;

import java.util.HashMap;
import java.util.List;


public class MockRealDB implements Database {
    private static MockRealDB instance;

    private final HashMap<String, Rider> riderDetails = new HashMap<>();
    private final HashMap<String, Driver> driverDetails = new HashMap<>();
    private final HashMap<String, Ride> rideDetails = new HashMap<>();
    private final HashMap<String, List<String>> riderDriverMapping = new HashMap<>();

    private MockRealDB() {}

    public static MockRealDB getInstance() {
        if (instance == null) {
            instance = new MockRealDB();
        }
        return instance;
    }

    @Override
    public void connect() {
        System.out.println("CONNECTED TO MOCK DATABASE");
    }

    public static void reset() {
        if (instance != null) {
            instance.riderDetails.clear();
            instance.rideDetails.clear();
            instance.driverDetails.clear();
            instance.riderDriverMapping.clear();
        }
    }

    @Override
    public HashMap<String, Rider> getRiderDetails() {
        return riderDetails;
    }

    @Override
    public HashMap<String, Driver> getDriverDetails() {
        return driverDetails;
    }

    @Override
    public HashMap<String, Ride> getRideDetails() {
        return rideDetails;
    }

    @Override
    public HashMap<String, List<String>> getRiderDriverMapping() {
        return riderDriverMapping;
    }
}
