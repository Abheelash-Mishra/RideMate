package org.example.database;

import org.example.models.Driver;
import org.example.models.Ride;
import org.example.models.Rider;

import java.util.HashMap;
import java.util.List;

public class InMemoryDB implements Database {
    private static InMemoryDB instance;

    public final HashMap<String, Rider> riderDetails = new HashMap<>();
    public final HashMap<String, Driver> driverDetails = new HashMap<>();
    public final HashMap<String, Ride> rideDetails = new HashMap<>();
    public final HashMap<String, List<String>> riderDriverMapping = new HashMap<>();

    private InMemoryDB() {} // Private constructor to prevent direct instantiation

    public static InMemoryDB getInstance() {
        if (instance == null) {
            instance = new InMemoryDB();
        }
        return instance;
    }

    public static void reset() {
        instance.riderDetails.clear();
        instance.rideDetails.clear();
        instance.driverDetails.clear();
        instance.riderDriverMapping.clear();
    }

    @Override
    public void connect() {
        System.out.println("CONNECTED TO IN-MEMORY DATABASE");
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
