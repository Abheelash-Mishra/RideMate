package services.admin.impl;

import database.Database;
import models.Driver;
import services.admin.AdminServiceInterface;
import services.admin.exceptions.InvalidDriverIDException;

public class AdminServiceConsoleImpl implements AdminServiceInterface {
    private final Database db;

    public AdminServiceConsoleImpl(Database db) {
        this.db = db;
    }

    @Override
    public void removeDriver(String driverID) {
        if (db.getDriverDetails().get(driverID) == null) {
            throw new InvalidDriverIDException();
        }

        db.getDriverDetails().remove(driverID);
        System.out.println("REMOVED_DRIVER " + driverID);
    }

    @Override
    public void listNDriverDetails(int N) {
        int size = Math.min(db.getDriverDetails().size(), N);
        int idx = 0;

        for (String driverID : db.getDriverDetails().keySet()) {
            if (idx == size) break;

            idx++;
            Driver driver = db.getDriverDetails().get(driverID);

            System.out.printf("DRIVER_%s (X=%d, Y=%d) RATING %.1f%n", driverID, driver.coordinates[0], driver.coordinates[1], driver.rating);
        }
    }

    @Override
    public void getDriverEarnings(String driverID) {
        Driver driver = db.getDriverDetails().get(driverID);

        System.out.printf("DRIVER_EARNINGS %s %.1f\n", driverID, driver.earnings);
    }
}
