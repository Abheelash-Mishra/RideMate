package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverDTO;
import org.example.dto.DriverEarningsDTO;
import org.example.models.Driver;
import org.example.exceptions.InvalidDriverIDException;
import org.example.repository.DriverRepository;
import org.example.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private DriverRepository driverRepository;

    @Override
    public boolean removeDriver(long driverID) {
        if (!driverRepository.existsById(driverID)) {
            throw new InvalidDriverIDException("Invalid Driver ID - " + driverID, new NoSuchElementException("Driver not present in database"));
        }

        try {
            log.info("Removing driver '{}' from the database", driverID);
            driverRepository.deleteById(driverID);
            return true;
        } catch (RuntimeException e) {
            log.error("Unexpected error while attempting to remove driver '{}' | Error: {}", driverID, e.getMessage(), e);
            throw new RuntimeException("Failed to remove driver " + driverID, e);
        }
    }


    @Override
    public List<DriverDTO> listNDriverDetails(int N) {
        try {
            Pageable topN = PageRequest.of(0, N);
            List<Driver> drivers = driverRepository.findTopNDrivers(topN);

            log.info("Retrieved first N drivers successfully");
            return drivers.stream()
                    .map(driver -> new DriverDTO(
                            driver.getDriverID(),
                            driver.getCoordinates().get(0),
                            driver.getCoordinates().get(1),
                            driver.getRating()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Unexpected error while fetching first {} drivers in database | Error: {}", N, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch driver details", e);
        }
    }


    @Override
    public DriverEarningsDTO getDriverEarnings(long driverID) {
        Driver driver = driverRepository.findById(driverID)
                .orElseThrow(() -> new InvalidDriverIDException("Invalid Driver ID - " + driverID, new NoSuchElementException("Driver not present in database")));

        log.info("Retrieved earnings of driver '{}' successfully", driverID);
        return new DriverEarningsDTO(driverID, driver.getEarnings());
    }
}
