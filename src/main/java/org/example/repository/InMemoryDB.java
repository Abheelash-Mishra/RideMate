package org.example.repository;

import org.example.models.Driver;
import org.example.models.Ride;
import org.example.models.Rider;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryDB implements Database {
    private final HashMap<String, Rider> riderDetails = new HashMap<>();
    private final HashMap<String, Driver> driverDetails = new HashMap<>();
    private final HashMap<String, Ride> rideDetails = new HashMap<>();
    private final HashMap<String, List<String>> riderDriverMapping = new HashMap<>();

    public void reset() {
        riderDetails.clear();
        driverDetails.clear();
        rideDetails.clear();
        riderDriverMapping.clear();
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
