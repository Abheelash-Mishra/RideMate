package services;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import models.Driver;
import models.Rider;
import utilities.DistanceUtility;

public class RideService {
    private HashMap<String, Rider> riderDetails = new HashMap<>();

    public void addRider(String riderID, int x_coordinate, int y_coordinate) {
        riderDetails.put(riderID, new Rider(riderID, x_coordinate, y_coordinate));

        System.out.println("Added new rider - " + riderID);
    }

    public matchRider(String riderID) {

    }
}

class DriverDistancePair {
    public int distance;
    public String ID;

    public DriverDistancePair(int distance, String ID) {
        this.distance = distance;
        this.ID = ID;
    }
}
