package database;

import models.Driver;
import models.Ride;
import models.Rider;

import java.util.HashMap;
import java.util.List;

public interface Database {
    void connect();

    HashMap<String, Rider> getRiderDetails();
    HashMap<String, Driver> getDriverDetails();
    HashMap<String, Ride> getRideDetails();
    HashMap<String, List<String>> getRiderDriverMapping();
}
