package org.example.services.admin;

import java.util.List;

public interface AdminService {
    String removeDriver(String driverID);
    List<String> listNDriverDetails(int N);
    float getDriverEarnings(String driverID);
}
