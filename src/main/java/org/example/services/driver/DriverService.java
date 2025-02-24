package org.example.services.driver;

public class DriverService {
    private final DriverServiceInterface driverServiceImpl;

    public DriverService(DriverServiceInterface driverServiceImpl) {
        this.driverServiceImpl = driverServiceImpl;
    }

    public void addDriver(String driverID, int x_coordinate, int y_coordinate) {
        driverServiceImpl.addDriver(driverID, x_coordinate, y_coordinate);
    }

    public void rateDriver(String driverID, float rating) {
        driverServiceImpl.rateDriver(driverID, rating);
    }
}
