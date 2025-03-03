package org.example.services.admin;

import org.example.dto.DriverDTO;
import org.example.dto.DriverEarningsDTO;
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
    public boolean removeDriver(String driverID) {
        if (db.getDriverDetails().get(driverID) == null) {
            throw new InvalidDriverIDException();
        }

        db.getDriverDetails().remove(driverID);
        return true;
    }

    @Override
    public List<DriverDTO> listNDriverDetails(int N) {
        List<DriverDTO> driverDetailsList = new ArrayList<>();
        int size = Math.min(db.getDriverDetails().size(), N);
        int idx = 0;

        for (String driverID : db.getDriverDetails().keySet()) {
            if (idx == size) break;
            idx++;

            Driver driver = db.getDriverDetails().get(driverID);
            driverDetailsList.add(new DriverDTO(
                    driverID,
                    driver.getCoordinates()[0],
                    driver.getCoordinates()[1],
                    driver.getRating()
            ));
        }

        return driverDetailsList;
    }


    @Override
    public DriverEarningsDTO getDriverEarnings(String driverID) {
        Driver driver = db.getDriverDetails().get(driverID);
        if (driver == null) {
            throw new InvalidDriverIDException();
        }
        return new DriverEarningsDTO(driverID, driver.getEarnings());
    }

}
