import services.DriverService;
import services.RideService;

import java.util.Scanner;

public class RiderApp {
    private static Scanner scanner = new Scanner(System.in);
    private static DriverService driverService = new DriverService();
    private static RideService rideService = new RideService();

    public static void reset() {
        rideService = new RideService();
        driverService = new DriverService();

        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        while(scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) break;

            processCommands(command);
        }

        scanner.close();
    }

    public static void processCommands(String command) {
        String[] parts = command.split(" ");
        int x_coordinate, y_coordinate;
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

                    rideService.matchRider(riderID, driverService);
                    break;

                case "START_RIDE":
                    rideID = parts[1];
                    int N = Integer.parseInt(parts[2]);
                    riderID = parts[3];

                    rideService.startRide(rideID, N, riderID, driverService);
                    break;

                case "STOP_RIDE":
                    rideID = parts[1];
                    int dest_x_coordinate = Integer.parseInt(parts[2]);
                    int dest_y_coordinate = Integer.parseInt(parts[3]);
                    int timeTakenInMins = Integer.parseInt(parts[4]);

                    rideService.stopRide(rideID,dest_x_coordinate, dest_y_coordinate, timeTakenInMins, driverService);
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

                    rideService.payViaWallet(rideID);
                    break;

                case "ADD_MONEY":
                    riderID = parts[1];
                    float amount = Float.parseFloat(parts[2]);

                    rideService.addMoney(riderID, amount);
                    break;

                default:
                    break;
            }
        }
        catch (RideService.InvalidRideException e) {
            System.out.println(e.getMessage());
        }
    }
}