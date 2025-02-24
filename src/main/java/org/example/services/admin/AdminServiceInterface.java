package org.example.services.admin;

public interface AdminServiceInterface {
    void removeDriver(String driverID);
    void listNDriverDetails(int N);
    void getDriverEarnings(String driverID);
}
