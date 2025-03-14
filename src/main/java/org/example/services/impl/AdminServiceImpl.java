package org.example.services.impl;

import org.example.dto.DriverDTO;
import org.example.dto.DriverEarningsDTO;
import org.example.models.Driver;
import org.example.exceptions.InvalidDriverIDException;
import org.example.repository.DriverRepository;
import org.example.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private DriverRepository driverRepository;

    @Override
    public boolean removeDriver(long driverID) {
        if (!driverRepository.existsById(driverID)) {
            throw new InvalidDriverIDException();
        }

        driverRepository.deleteById(driverID);
        return true;
    }

    @Override
    public List<DriverDTO> listNDriverDetails(int N) {
        List<Driver> drivers = driverRepository.findTopNDrivers(N);

        return drivers.stream()
                .map(driver -> new DriverDTO(
                        driver.getDriverID(),
                        driver.getCoordinates().get(0),
                        driver.getCoordinates().get(1),
                        driver.getRating()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public DriverEarningsDTO getDriverEarnings(long driverID) {
        log.info("Fetching details of driver {}...", driverID);
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> {
                    log.warn("Driver {} does not exist!", driverID);
                    return new InvalidDriverIDException(driverID);
                });

        return new DriverEarningsDTO(driverID, driver.getEarnings());
    }
}
