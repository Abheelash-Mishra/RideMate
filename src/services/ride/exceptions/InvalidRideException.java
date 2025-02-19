package services.ride.exceptions;

public class InvalidRideException extends RuntimeException {
    public InvalidRideException() {
        super("INVALID_RIDE");
    }
}
