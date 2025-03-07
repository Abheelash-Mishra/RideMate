package org.example;

import org.example.config.AppConfig;
import org.example.dto.DriverDTO;
import org.example.exceptions.NoDriversException;
import org.example.models.Payment;
import org.example.models.PaymentStatus;
import org.example.models.Ride;
import org.example.repository.Database;
import org.example.exceptions.InvalidDriverIDException;
import org.example.services.admin.AdminService;
import org.example.services.admin.AdminServiceImpl;
import org.example.services.driver.DriverService;
import org.example.services.driver.DriverServiceImpl;
import org.example.services.payment.PaymentMethodType;
import org.example.services.payment.PaymentService;
import org.example.services.payment.impl.WalletPayment;
import org.example.exceptions.InvalidRideException;
import org.example.services.ride.RideService;
import org.example.services.ride.RideServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Scanner;

public class RiderApp {
    public static AnnotationConfigApplicationContext context;
    private static Scanner scanner = new Scanner(System.in);

    @Autowired
    private static Database db;
    private static AdminService adminService;
    private static RideService rideService;
    private static DriverService driverService;
    private static PaymentService paymentService;

    public static void reset() {
        scanner = new Scanner(System.in);
    }

    public static void initContext() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);

        adminService = context.getBean(AdminServiceImpl.class);
        rideService = context.getBean(RideServiceImpl.class);
        driverService = context.getBean(DriverServiceImpl.class);
        paymentService = context.getBean(PaymentService.class);
    }

    public static void main(String[] args) {
        initContext();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) break;

            processCommands(command);
        }

        scanner.close();
    }

    public static void processCommands(String command) {
        db = context.getBean(Database.class);
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

                    List<String> matchedDrivers = rideService.matchRider(riderID).matchedDrivers();
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

                    float newRating = (float) driverService.rateDriver(driverID, rating).get("rating");
                    System.out.println("CURRENT_RATING " + driverID + " " + newRating);
                    break;

                case "BILL":
                    rideID = parts[1];

                    double bill = rideService.billRide(rideID);
                    Ride currentRide = db.getRideDetails().get(rideID);
                    System.out.printf("BILL %s %s %.1f\n", rideID, currentRide.getDriverID(), bill);
                    break;

                case "PAY":
                    rideID = parts[1];
                    String type = parts[2];

                    PaymentMethodType paymentMethodType = PaymentMethodType.valueOf(type.toUpperCase());
                    paymentService.setPaymentMethod(paymentMethodType);

                    Payment paymentDetails = paymentService.processPayment(rideID);
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
                        System.out.printf("DRIVER_%s (X=%d, Y=%d) RATING %.1f", driver.driverID(), driver.x(), driver.y(), driver.rating());
                        System.out.println();
                    }
                    break;

                case "ADMIN_VIEW_DRIVER_EARNINGS":
                    driverID = parts[1];

                    float earnings = adminService.getDriverEarnings(driverID).earnings();
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