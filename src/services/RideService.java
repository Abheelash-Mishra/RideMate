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

        System.err.println("Added new rider - " + riderID);
    }

    public void matchRider(String riderID, DriverService driverService) {
        final double LIMIT = 5.0;
        int[] riderCoordinates = riderDetails.get(riderID).coordinates;

        HashMap<String, Driver> allDrivers = driverService.driverDetails;
        PriorityQueue<DriverDistancePair> nearestDrivers = new PriorityQueue<>((pair1, pair2) -> {
            if (pair1.distance < pair2.distance) {
                return -1;
            }
            else if (pair1.distance > pair2.distance) {
                return 1;
            }

            return pair1.ID.compareTo(pair2.ID);
        });

        for(String driverID : allDrivers.keySet()) {
            Driver driver = allDrivers.get(driverID);
            if(driver.available) {
                double distance = DistanceUtility.calculate(riderCoordinates, driver.coordinates);
                System.err.println(distance);

                if (distance <= LIMIT) {
                    DriverDistancePair pair = new DriverDistancePair(driverID, distance);
                    nearestDrivers.add(pair);
                }
            }
        }

        driversMatched(nearestDrivers);
    }

    public void driversMatched(PriorityQueue<DriverDistancePair> nearestDrivers) {
        if (nearestDrivers.isEmpty()) {
            System.out.println("NO_DRIVERS_AVAILABLE");
            return;
        }

        int size = Math.min(nearestDrivers.size(), 5);
        System.out.print("DRIVERS_MATCHED ");

        for (int i = 0; i < size; i++) {
            DriverDistancePair driver = nearestDrivers.poll();
            if (driver != null) {
                System.out.print(driver.ID + " ");
            }
        }
        System.out.println();
    }


    public static class DriverDistancePair {
        public String ID;
        public double distance;

        public DriverDistancePair(String ID, double distance) {
            this.ID = ID;
            this.distance = distance;
        }
    }
}
