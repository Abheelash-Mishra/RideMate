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

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Slf4j
@Component
public class RiderAppCLI {

    private Scanner scanner = new Scanner(System.in);
    private static final HashMap<Command, String> usageMsg = new HashMap<>();

    static {
        usageMsg.put(Command.ADD_DRIVER, "Expectation: ADD_DRIVER <DRIVER ID> <X COORDINATE> <Y COORDINATE>");
        usageMsg.put(Command.ADD_RIDER, "Expectation: ADD_RIDER <RIDER ID> <X COORDINATE> <Y COORDINATE>");
        usageMsg.put(Command.MATCH, "Expectation: MATCH <RIDER ID>");
        usageMsg.put(Command.START_RIDE, "Expectation: START_RIDE <RIDE ID> <N> <RIDER ID>");
        usageMsg.put(Command.STOP_RIDE, "Expectation: STOP_RIDE <RIDE ID> <DESTINATION X COORDINATE> <DESTINATION Y COORDINATE> <RIDE TIME IN MINUTES>");
        usageMsg.put(Command.RATE_DRIVER, "Expectation: RATE_DRIVER <DRIVER ID> <RATING>");
        usageMsg.put(Command.BILL, "Expectation: BILL <RIDE ID> <N> <RIDER ID>");
        usageMsg.put(Command.PAY, "Expectation: PAY <RIDE ID> <PAYMENT METHOD>");
        usageMsg.put(Command.ADD_MONEY, "Expectation: ADD_MONEY <RIDER ID> <AMOUNT>");
        usageMsg.put(Command.ADMIN_REMOVE_DRIVER, "Expectation: ADMIN_REMOVE_DRIVER <DRIVER ID>");
        usageMsg.put(Command.ADMIN_LIST_DRIVERS, "Expectation: ADMIN_LIST_DRIVERS <N>");
        usageMsg.put(Command.ADMIN_VIEW_DRIVER_EARNINGS, "Expectation: ADMIN_VIEW_DRIVER_EARNINGS <DRIVER ID>");
    }

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
        log.info("CLI Mode Activated. Type `QUIT` to exit.");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            processCommands(input);
        }

        scanner.close();
    }

    public void processCommands(String input) {
        Command command = null;
        try {
            String[] parts = input.split(" ");

            command = Command.valueOf(parts[0].toUpperCase());

            int x_coordinate, y_coordinate, N;
            long riderID, rideID, driverID;
            StringBuilder output;

            switch (command) {
                case ADD_DRIVER:
                    driverID = Long.parseLong(parts[1]);
                    x_coordinate = Integer.parseInt(parts[2]);
                    y_coordinate = Integer.parseInt(parts[3]);

                    driverService.addDriver(driverID, x_coordinate, y_coordinate);
                    break;

                case ADD_RIDER:
                    riderID = Long.parseLong(parts[1]);
                    x_coordinate = Integer.parseInt(parts[2]);
                    y_coordinate = Integer.parseInt(parts[3]);

                    rideService.addRider(riderID, x_coordinate, y_coordinate);
                    break;

                case MATCH:
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

                case START_RIDE:
                    rideID = Long.parseLong(parts[1]);
                    N = Integer.parseInt(parts[2]);
                    riderID = Long.parseLong(parts[3]);

                    rideService.startRide(rideID, N, riderID);
                    log.info("RIDE_STARTED {}", rideID);
                    break;

                case STOP_RIDE:
                    rideID = Long.parseLong(parts[1]);
                    int dest_x_coordinate = Integer.parseInt(parts[2]);
                    int dest_y_coordinate = Integer.parseInt(parts[3]);
                    int timeTakenInMins = Integer.parseInt(parts[4]);

                    rideService.stopRide(rideID, dest_x_coordinate, dest_y_coordinate, timeTakenInMins);
                    log.info("RIDE_STOPPED {}", rideID);
                    break;

                case RATE_DRIVER:
                    driverID = Long.parseLong(parts[1]);
                    float rating = Float.parseFloat(parts[2]);

                    float newRating = driverService.rateDriver(driverID, rating).getRating();
                    log.info("CURRENT_RATING {} {}", driverID, newRating);
                    break;

                case BILL:
                    rideID = Long.parseLong(parts[1]);

                    rideService.billRide(rideID);
                    Ride currentRide = rideRepository.findById(rideID)
                            .orElseThrow(() -> new InvalidRideException("Invalid Ride ID - " + rideID, new NoSuchElementException("Ride not present in database")));

                    log.info("BILL {} {} {}", rideID, currentRide.getDriver().getDriverID(), currentRide.getBill());
                    break;

                case PAY:
                    rideID = Long.parseLong(parts[1]);
                    String type = parts[2];

                    PaymentMethodType paymentMethodType = PaymentMethodType.valueOf(type.toUpperCase());

                    PaymentDetailsDTO paymentDetails = paymentService.processPayment(rideID, paymentMethodType);
                    if (paymentDetails.getPaymentStatus() == PaymentStatus.FAILED) {
                        log.info("LOW_BALANCE");
                    } else {
                        log.info("PAID {} {} VIA {}", paymentDetails.getReceiverID(), paymentDetails.getAmount(), paymentMethodType);
                    }
                    break;

                case ADD_MONEY:
                    riderID = Long.parseLong(parts[1]);
                    float amount = Float.parseFloat(parts[2]);

                    float balance = paymentService.addMoney(riderID, amount);
                    log.info("CURRENT_BALANCE {} {}", riderID, balance);
                    break;

                case ADMIN_REMOVE_DRIVER:
                    driverID = Long.parseLong(parts[1]);

                    if (adminService.removeDriver(driverID)) {
                        log.info("REMOVED_DRIVER {}", driverID);
                    }
                    break;

                case ADMIN_LIST_DRIVERS:
                    N = Integer.parseInt(parts[1]);

                    List<DriverDTO> driverDetails = adminService.listNDriverDetails(N);
                    for (DriverDTO driver: driverDetails) {
                        log.info("DRIVER_{} (X={}, Y={}) RATING {}", driver.getDriverID(), driver.getX(), driver.getY(), driver.getRating());
                    }
                    break;

                case ADMIN_VIEW_DRIVER_EARNINGS:
                    driverID = Long.parseLong(parts[1]);

                    float earnings = adminService.getDriverEarnings(driverID).getEarnings();
                    log.info("DRIVER_EARNINGS {} {}", driverID, earnings);
                    break;

                case QUIT:
                    break;
            }
        } catch (InvalidRideException | InvalidDriverIDException | NoDriversException e) {
            log.warn("An error occurred || Exception: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            if (command == null) {
                log.warn("No valid command was entered");
            }
            else {
                log.warn("Invalid parameters entered || Exception: {}", e.getMessage());
                log.warn("{}", usageMsg.get(command));
            }
        } catch (RuntimeException e) {
            log.warn("Something went wrong unexpectedly || Exception: {}", e.getMessage());
        }
    }

    private enum Command {
        ADD_DRIVER,
        ADD_RIDER,
        MATCH,
        START_RIDE,
        STOP_RIDE,
        RATE_DRIVER,
        BILL,
        PAY,
        ADD_MONEY,
        ADMIN_REMOVE_DRIVER,
        ADMIN_LIST_DRIVERS,
        ADMIN_VIEW_DRIVER_EARNINGS,
        QUIT
    }
}

