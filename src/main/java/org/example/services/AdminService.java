package org.example.services;

import org.example.dto.DriverDTO;
import org.example.dto.DriverEarningsDTO;

import java.util.List;

public interface AdminService {
    boolean removeDriver(long driverID);
    List<DriverDTO> listNDriverDetails(int N);
    DriverEarningsDTO getDriverEarnings(long driverID);
}
