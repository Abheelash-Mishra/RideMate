package org.example.repository;

import org.example.models.Driver;
import org.example.models.Ride;
import org.example.models.Rider;

import java.util.HashMap;
import java.util.List;

public interface Database {
    void connect();
    void reset();

    HashMap<String, Rider> getRiderDetails();
    HashMap<String, Driver> getDriverDetails();
    HashMap<String, Ride> getRideDetails();
    HashMap<String, List<String>> getRiderDriverMapping();
}
