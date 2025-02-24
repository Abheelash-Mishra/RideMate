package org.example;

import org.example.config.AppConfig;
import org.example.repository.Database;
import org.example.repository.InMemoryDB;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

public class RiderApp {
    private static final ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    private static Scanner scanner = new Scanner(System.in);

    @Autowired
    private static Database db;
    private static final AdminService adminService = context.getBean(AdminServiceImpl.class);
    private static final RideService rideService = context.getBean(RideServiceImpl.class);
    private static final DriverService driverService = context.getBean(DriverServiceImpl.class);
    private static final PaymentService paymentService = context.getBean(PaymentService.class);

    public static void reset() {
        db.reset();

        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) break;

            processCommands(command);
        }

        scanner.close();
    }

    public static void processCommands(String command) {
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

                    rideService.matchRider(riderID);
                    break;

                case "START_RIDE":
                    rideID = parts[1];
                    N = Integer.parseInt(parts[2]);
                    riderID = parts[3];

                    rideService.startRide(rideID, N, riderID);
                    break;

                case "STOP_RIDE":
                    rideID = parts[1];
                    int dest_x_coordinate = Integer.parseInt(parts[2]);
                    int dest_y_coordinate = Integer.parseInt(parts[3]);
                    int timeTakenInMins = Integer.parseInt(parts[4]);

                    rideService.stopRide(rideID, dest_x_coordinate, dest_y_coordinate, timeTakenInMins);
                    break;

                case "RATE_DRIVER":
                    driverID = parts[1];
                    float rating = Float.parseFloat(parts[2]);

                    driverService.rateDriver(driverID, rating);
                    break;

                case "BILL":
                    rideID = parts[1];

                    rideService.billRide(rideID);
                    break;

                case "PAY":
                    rideID = parts[1];
                    String method = parts[2];

                    PaymentMethodType paymentMethodType = PaymentMethodType.valueOf(method.toUpperCase());
                    paymentService.setPaymentMethod(paymentMethodType);

                    paymentService.processPayment(rideID);
                    break;

                case "ADD_MONEY":
                    riderID = parts[1];
                    float amount = Float.parseFloat(parts[2]);

                    paymentService.setPaymentMethod(PaymentMethodType.WALLET);
                    WalletPayment wallet = (WalletPayment) paymentService.getPaymentMethod();
                    wallet.addMoney(riderID, amount);
                    break;

                case "ADMIN_REMOVE_DRIVER":
                    driverID = parts[1];

                    adminService.removeDriver(driverID);
                    break;

                case "ADMIN_LIST_DRIVERS":
                    N = Integer.parseInt(parts[1]);

                    adminService.listNDriverDetails(N);
                    break;

                case "ADMIN_VIEW_DRIVER_EARNINGS":
                    driverID = parts[1];

                    adminService.getDriverEarnings(driverID);
                    break;

                default:
                    break;
            }
        } catch (InvalidRideException | InvalidDriverIDException e) {
            System.out.println(e.getMessage());
        }
    }
}