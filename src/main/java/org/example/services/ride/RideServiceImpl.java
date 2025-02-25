package org.example.services.ride;

import org.example.repository.Database;
import org.example.models.Driver;
import org.example.models.Ride;
import org.example.models.Rider;

import org.example.exceptions.InvalidRideException;
import org.example.exceptions.NoDriversException;
import org.example.utilities.DistanceUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;


public class RideServiceImpl implements RideService {
    private final Database db;

    @Autowired
    public RideServiceImpl(Database db) {
        this.db = db;
    }

    @Override
    public void addRider(String riderID, int x_coordinate, int y_coordinate) {
        db.getRiderDetails().put(riderID, new Rider(x_coordinate, y_coordinate));
    }

    @Override
    public void matchRider(String riderID) {
        final double LIMIT = 5.0;
        int[] riderCoordinates = db.getRiderDetails().get(riderID).getCoordinates();

        HashMap<String, Driver> allDrivers = db.getDriverDetails();
        PriorityQueue<DriverDistancePair> nearestDrivers = new PriorityQueue<>((pair1, pair2) -> {
            if (pair1.distance < pair2.distance) {
                return -1;
            } else if (pair1.distance > pair2.distance) {
                return 1;
            }

            return pair1.ID.compareTo(pair2.ID);
        });

        for (String driverID : allDrivers.keySet()) {
            Driver driver = allDrivers.get(driverID);

            if (driver.isAvailable()) {
                double distance = DistanceUtility.calculate(riderCoordinates, driver.getCoordinates());

                if (distance <= LIMIT) {
                    DriverDistancePair pair = new DriverDistancePair(driverID, distance);
                    nearestDrivers.add(pair);
                }
            }
        }

        try {
            driversMatched(riderID, nearestDrivers);
        } catch (NoDriversException e) {
            System.out.println(e.getMessage());
        }
    }

    private void driversMatched(String riderID, PriorityQueue<DriverDistancePair> nearestDrivers) throws NoDriversException {
        if (nearestDrivers.isEmpty()) {
            throw new NoDriversException();
        }

        System.out.print("DRIVERS_MATCHED");
        db.getRiderDriverMapping().putIfAbsent(riderID, new ArrayList<>());
        int size = Math.min(nearestDrivers.size(), 5);

        for (int i = 0; i < size; i++) {
            DriverDistancePair driver = nearestDrivers.poll();
            if (driver != null) {
                db.getRiderDriverMapping().get(riderID).add(driver.ID);
                System.out.print(" " + driver.ID);
            }
        }
        System.out.println();
    }

    @Override
    public void startRide(String rideID, int N, String riderID) {
        List<String> matchedDrivers = db.getRiderDriverMapping().get(riderID);

        if (matchedDrivers.size() < N) {
            throw new InvalidRideException();
        }

        String driverID = matchedDrivers.get(N - 1);
        boolean driverAvailable = db.getDriverDetails().get(driverID).isAvailable();

        if (!driverAvailable || db.getRideDetails().containsKey(rideID)) {
            throw new InvalidRideException();
        }

        db.getRideDetails().put(rideID, new Ride(riderID, driverID));
        db.getDriverDetails().get(driverID).updateAvailability();

        System.out.println("RIDE_STARTED " + rideID);
    }

    @Override
    public void stopRide(String rideID, int dest_x_coordinate, int dest_y_coordinate, int timeTakenInMins) {
        Ride currentRide = db.getRideDetails().get(rideID);
        if (currentRide == null || currentRide.isFinished()) {
            throw new InvalidRideException();
        }

        System.out.println("RIDE_STOPPED " + rideID);
        db.getDriverDetails().get(currentRide.getDriverID()).updateAvailability();
        currentRide.finishRide(dest_x_coordinate, dest_y_coordinate, timeTakenInMins);
    }

    @Override
    public void billRide(String rideID) {
        Ride currentRide = db.getRideDetails().get(rideID);
        if (currentRide == null || !currentRide.isFinished()) {
            throw new InvalidRideException();
        }

        final double BASE_FARE = 50.0;
        final double PER_KM = 6.5;
        final double PER_MIN = 2.0;
        final double SERVICE_TAX = 1.2;      // 20 percent

        double finalBill = BASE_FARE;

        int[] startCoordinates = db.getRiderDetails().get(currentRide.getRiderID()).getCoordinates();
        int[] destCoordinates = currentRide.getDestinationCoordinates();
        double distanceTravelled = DistanceUtility.calculate(startCoordinates, destCoordinates);
        finalBill += (distanceTravelled * PER_KM);

        int timeTakenInMins = currentRide.getTimeTakenInMins();
        finalBill += (timeTakenInMins * PER_MIN);

        finalBill *= SERVICE_TAX;

        currentRide.setBill((float) (Math.round(finalBill * 10.0) / 10.0));
        System.out.printf("BILL %s %s %.1f%n", rideID, currentRide.getDriverID(), finalBill);
    }

    public record DriverDistancePair(String ID, double distance) {
    }
}
