package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverDTO;
import org.example.dto.PaymentDetailsDTO;
import org.example.exceptions.*;
import org.example.models.PaymentMethodType;
import org.example.models.PaymentStatus;
import org.example.models.Ride;
import org.example.repository.RideRepository;
import org.example.services.AdminService;
import org.example.services.DriverService;
import org.example.services.PaymentService;
import org.example.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Slf4j
@Component
public class RiderAppCLI {

    private Scanner scanner = new Scanner(System.in);

    @Autowired
    private AdminService adminService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private RideService rideService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RideRepository rideRepository;

    public void reset() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("CLI Mode Activated. Press `Enter` without any command typed in to quit.");

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) break;

            processCommands(command);
        }

        scanner.close();
    }

    public void processCommands(String command) {
        String[] parts = command.split(" ");

        int x_coordinate, y_coordinate, N;
        long riderID, rideID, driverID;

        StringBuilder output;

        try {
            switch (parts[0]) {
                case "ADD_DRIVER":
                    driverID = Long.parseLong(parts[1]);
                    x_coordinate = Integer.parseInt(parts[2]);
                    y_coordinate = Integer.parseInt(parts[3]);

                    driverService.addDriver(driverID, x_coordinate, y_coordinate);
                    break;

                case "ADD_RIDER":
                    riderID = Long.parseLong(parts[1]);
                    x_coordinate = Integer.parseInt(parts[2]);
                    y_coordinate = Integer.parseInt(parts[3]);

                    rideService.addRider(riderID, x_coordinate, y_coordinate);
                    break;

                case "MATCH":
                    riderID = Long.parseLong(parts[1]);

                    List<Long> matchedDrivers = rideService.matchRider(riderID).getMatchedDrivers();
                    if (matchedDrivers.isEmpty()) {
                        throw new NoDriversException();
                    }

                    output = new StringBuilder("DRIVERS_MATCHED");
                    for (long matchedDriver : matchedDrivers) {
                        output.append(" ").append(matchedDriver);
                    }

                    log.info(String.valueOf(output));
                    break;

                case "START_RIDE":
                    rideID = Long.parseLong(parts[1]);
                    N = Integer.parseInt(parts[2]);
                    riderID = Long.parseLong(parts[3]);

                    rideService.startRide(rideID, N, riderID);
                    log.info("RIDE_STARTED {}", rideID);
                    break;

                case "STOP_RIDE":
                    rideID = Long.parseLong(parts[1]);
                    int dest_x_coordinate = Integer.parseInt(parts[2]);
                    int dest_y_coordinate = Integer.parseInt(parts[3]);
                    int timeTakenInMins = Integer.parseInt(parts[4]);

                    rideService.stopRide(rideID, dest_x_coordinate, dest_y_coordinate, timeTakenInMins);
                    log.info("RIDE_STOPPED {}", rideID);
                    break;

                case "RATE_DRIVER":
                    driverID = Long.parseLong(parts[1]);
                    float rating = Float.parseFloat(parts[2]);

                    float newRating = driverService.rateDriver(driverID, rating).getRating();
                    log.info("CURRENT_RATING {} {}", driverID, newRating);
                    break;

                case "BILL":
                    rideID = Long.parseLong(parts[1]);

                    rideService.billRide(rideID);
                    Ride currentRide = rideRepository.findById(rideID)
                            .orElseThrow(InvalidRideException::new);

                    log.info("BILL {} {} {}", rideID, currentRide.getDriver().getDriverID(), currentRide.getBill());
                    break;

                case "PAY":
                    rideID = Long.parseLong(parts[1]);
                    String type = parts[2];

                    PaymentMethodType paymentMethodType = PaymentMethodType.valueOf(type.toUpperCase());

                    PaymentDetailsDTO paymentDetails = paymentService.processPayment(rideID, paymentMethodType);
                    if (paymentDetails.getPaymentStatus() == PaymentStatus.FAILED) {
                        log.info("LOW_BALANCE");
                    }
                    else {
                        log.info("PAID {} {} VIA {}", paymentDetails.getReceiverID(), paymentDetails.getAmount(), paymentMethodType);
                    }
                    break;

                case "ADD_MONEY":
                    riderID = Long.parseLong(parts[1]);
                    float amount = Float.parseFloat(parts[2]);

                    float balance = paymentService.addMoney(riderID, amount);
                    log.info("CURRENT_BALANCE {} {}", riderID, balance);
                    break;

                case "ADMIN_REMOVE_DRIVER":
                    driverID = Long.parseLong(parts[1]);

                    if (adminService.removeDriver(driverID)) {
                        log.info("REMOVED_DRIVER {}", driverID);
                    }
                    break;

                case "ADMIN_LIST_DRIVERS":
                    N = Integer.parseInt(parts[1]);

                    List<DriverDTO> driverDetails = adminService.listNDriverDetails(N);
                    for (DriverDTO driver: driverDetails) {
                        log.info("DRIVER_{} (X={}, Y={}) RATING {}", driver.getDriverID(), driver.getX(), driver.getY(), driver.getRating());
                    }
                    break;

                case "ADMIN_VIEW_DRIVER_EARNINGS":
                    driverID = Long.parseLong(parts[1]);

                    float earnings = adminService.getDriverEarnings(driverID).getEarnings();
                    log.info("DRIVER_EARNINGS {} {}", driverID, earnings);
                    break;

                default:
                    break;
            }
        } catch (InvalidRideException | InvalidDriverIDException | NoDriversException e) {
            log.warn("An error occurred | Exception: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Something went wrong unexpectedly | Exception: {}", e.getMessage(), e);
        }
    }
}

