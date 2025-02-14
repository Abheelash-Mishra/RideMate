import database.InMemoryDB;
import services.AdminService;
import services.DriverService;
import services.RideService;

import java.util.Scanner;

public class RiderApp {
    private static Scanner scanner = new Scanner(System.in);

    private static final InMemoryDB db = InMemoryDB.getInstance();
    private static final AdminService admin = new AdminService(db);
    private static final RideService rideService = new RideService(db);
    private static final DriverService driverService = new DriverService(db);

    public static void reset() {
        InMemoryDB.reset();

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

                case "PAY_VIA_WALLET":
                    rideID = parts[1];

                    rideService.getPaymentService().payViaWallet(rideID);
                    break;

                case "ADD_MONEY":
                    riderID = parts[1];
                    float amount = Float.parseFloat(parts[2]);

                    rideService.getPaymentService().addMoney(riderID, amount);
                    break;

                case "ADMIN_REMOVE_DRIVER":
                    driverID = parts[1];

                    admin.removeDriver(driverID);
                    break;

                case "ADMIN_LIST_DRIVERS":
                    N = Integer.parseInt(parts[1]);

                    admin.listNDriverDetails(N);
                    break;

                default:
                    break;
            }
        } catch (RideService.InvalidRideException | AdminService.InvalidDriverIDException e) {
            System.out.println(e.getMessage());
        }
    }
}