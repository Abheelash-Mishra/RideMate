package org.example.services.admin;

import org.example.services.admin.exceptions.InvalidDriverIDException;

public class AdminService {
    private final AdminServiceInterface adminServiceImpl;

    public AdminService(AdminServiceInterface adminServiceImpl) {
        this.adminServiceImpl = adminServiceImpl;
    }

    public void removeDriver(String driverID) throws InvalidDriverIDException {
        adminServiceImpl.removeDriver(driverID);
    }

    public void listNDriverDetails(int N) {
        adminServiceImpl.listNDriverDetails(N);
    }

    public void getDriverEarnings(String driverID) {
        adminServiceImpl.getDriverEarnings(driverID);
    }
}
