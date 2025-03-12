package org.example;

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
import org.example.services.impl.WalletPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

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
        String riderID, rideID, driverID;

        try {
            switch (parts[0]) {
                case "ADD_DRIVER":
                    driverID = parts[1];
                    x_coordinate = Integer.parseInt(parts[2]);
                    y_coordinate = Integer.parseInt(parts[3]);

                    driverService.addDriver(driverID, x_coordinate, y_coordinate);
                    break;

                case "ADD_RIDER":
                    riderID = parts[1];
                    x_coordinate = Integer.parseInt(parts[2]);
                    y_coordinate = Integer.parseInt(parts[3]);

                    rideService.addRider(riderID, x_coordinate, y_coordinate);
                    break;

                case "MATCH":
                    riderID = parts[1];

                    List<String> matchedDrivers = rideService.matchRider(riderID).getMatchedDrivers();
                    if (matchedDrivers.isEmpty()) {
                        throw new NoDriversException();
                    }

                    System.out.print("DRIVERS_MATCHED");
                    for (String matchedDriver : matchedDrivers) {
                        System.out.print(" " + matchedDriver);
                    }
                    System.out.println();

                    break;

                case "START_RIDE":
                    rideID = parts[1];
                    N = Integer.parseInt(parts[2]);
                    riderID = parts[3];

                    rideService.startRide(rideID, N, riderID);
                    System.out.println("RIDE_STARTED " + rideID);
                    break;

                case "STOP_RIDE":
                    rideID = parts[1];
                    int dest_x_coordinate = Integer.parseInt(parts[2]);
                    int dest_y_coordinate = Integer.parseInt(parts[3]);
                    int timeTakenInMins = Integer.parseInt(parts[4]);

                    rideService.stopRide(rideID, dest_x_coordinate, dest_y_coordinate, timeTakenInMins);
                    System.out.println("RIDE_STOPPED " + rideID);
                    break;

                case "RATE_DRIVER":
                    driverID = parts[1];
                    float rating = Float.parseFloat(parts[2]);

                    float newRating = driverService.rateDriver(driverID, rating).getRating();
                    System.out.println("CURRENT_RATING " + driverID + " " + newRating);
                    break;

                case "BILL":
                    rideID = parts[1];

                    double bill = rideService.billRide(rideID);
                    Ride currentRide = rideRepository.findById(rideID)
                            .orElseThrow(InvalidRideException::new);

                    System.out.printf("BILL %s %s %.1f\n", rideID, currentRide.getDriver().getDriverID(), bill);
                    break;

                case "PAY":
                    rideID = parts[1];
                    String type = parts[2];

                    PaymentMethodType paymentMethodType = PaymentMethodType.valueOf(type.toUpperCase());
                    paymentService.setPaymentMethod(paymentMethodType);

                    PaymentDetailsDTO paymentDetails = paymentService.processPayment(rideID);
                    if (paymentDetails.getPaymentStatus() == PaymentStatus.FAILED) {
                        System.out.println("LOW_BALANCE");
                    }
                    else {
                        System.out.printf("PAID %s %.1f VIA %s\n", paymentDetails.getReceiverID(), paymentDetails.getAmount(), paymentMethodType);
                    }
                    break;

                case "ADD_MONEY":
                    riderID = parts[1];
                    float amount = Float.parseFloat(parts[2]);

                    paymentService.setPaymentMethod(PaymentMethodType.WALLET);
                    WalletPayment wallet = (WalletPayment) paymentService.getPaymentMethod();

                    float balance = wallet.addMoney(riderID, amount);
                    System.out.println("CURRENT_BALANCE " + riderID + " " + balance);
                    break;

                case "ADMIN_REMOVE_DRIVER":
                    driverID = parts[1];

                    if (adminService.removeDriver(driverID)) System.out.println("REMOVED_DRIVER " + driverID);
                    break;

                case "ADMIN_LIST_DRIVERS":
                    N = Integer.parseInt(parts[1]);

                    List<DriverDTO> driverDetails = adminService.listNDriverDetails(N);
                    for (DriverDTO driver: driverDetails) {
                        System.out.printf("DRIVER_%s (X=%d, Y=%d) RATING %.1f", driver.getDriverID(), driver.getX(), driver.getY(), driver.getRating());
                        System.out.println();
                    }
                    break;

                case "ADMIN_VIEW_DRIVER_EARNINGS":
                    driverID = parts[1];

                    float earnings = adminService.getDriverEarnings(driverID).getEarnings();
                    System.out.printf("DRIVER_EARNINGS %s %.1f\n", driverID, earnings);
                    break;

                default:
                    break;
            }
        } catch (InvalidRideException | InvalidDriverIDException | NoDriversException e) {
            System.out.println(e.getMessage());
        }
    }
}

