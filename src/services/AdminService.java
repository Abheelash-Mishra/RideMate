package services;

import database.Database;
import models.Driver;

public class AdminService {
    private final Database db;

    public AdminService(Database db) {
        this.db = db;
    }

    public void removeDriver(String driverID) throws InvalidDriverIDException {
        if (db.getDriverDetails().get(driverID) == null) {
            throw new InvalidDriverIDException();
        }

        db.getDriverDetails().remove(driverID);
        System.out.println("REMOVED_DRIVER " + driverID);
    }

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

    public void getDriverEarnings(String driverID) {
        Driver driver = db.getDriverDetails().get(driverID);
        System.out.printf("DRIVER_EARNINGS %s %.1f\n", driverID, driver.earnings);
    }


    public static class InvalidDriverIDException extends Exception {
        public InvalidDriverIDException() {
            super("INVALID_DRIVER_ID");
        }
    }
}
