package org.example.services.admin;

import org.example.repository.Database;
import org.example.models.Driver;
import org.example.exceptions.InvalidDriverIDException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final Database db;

    @Autowired
    public AdminServiceImpl(Database db) {
        this.db = db;
    }

    @Override
    public String removeDriver(String driverID) {
        if (db.getDriverDetails().get(driverID) == null) {
            throw new InvalidDriverIDException();
        }

        db.getDriverDetails().remove(driverID);
        return "REMOVED_DRIVER " + driverID;
    }

    @Override
    public List<String> listNDriverDetails(int N) {
        List<String> driverDetailsList = new ArrayList<>();
        int size = Math.min(db.getDriverDetails().size(), N);
        int idx = 0;

        for (String driverID : db.getDriverDetails().keySet()) {
            if (idx == size) break;
            idx++;

            Driver driver = db.getDriverDetails().get(driverID);
            String driverInfo = String.format(
                    "DRIVER_%s (X=%d, Y=%d) RATING %.1f",
                    driverID, driver.getCoordinates()[0], driver.getCoordinates()[1], driver.getRating()
            );

            driverDetailsList.add(driverInfo);
        }

        return driverDetailsList;
    }


    @Override
    public float getDriverEarnings(String driverID) {
        Driver driver = db.getDriverDetails().get(driverID);

        return driver.getEarnings();
    }
}
