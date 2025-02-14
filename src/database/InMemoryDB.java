package database;

import models.Driver;
import models.Ride;
import models.Rider;

import java.util.HashMap;
import java.util.List;

public class InMemoryDB {
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
}
