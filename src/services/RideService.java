package services;

import models.Rider;
import java.util.HashMap;

public class RideService {
    private HashMap<String, Rider> riderDetails = new HashMap<>();

    public void addRider(String riderID, int x_coordinate, int y_coordinate) {
        riderDetails.put(riderID, new Rider(riderID, x_coordinate, y_coordinate));

        System.out.println("Added new rider - " + riderID);
    }

    public matchRider(String riderID) {

    }
}
