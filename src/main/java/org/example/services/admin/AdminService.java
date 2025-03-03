package org.example.services.admin;

import org.example.dto.DriverDTO;
import org.example.dto.DriverEarningsDTO;

import java.util.List;

public interface AdminService {
    boolean removeDriver(String driverID);
    List<DriverDTO> listNDriverDetails(int N);
    DriverEarningsDTO getDriverEarnings(String driverID);
}
