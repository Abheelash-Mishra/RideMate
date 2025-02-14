package services;

import models.Ride;
import models.Rider;

import java.util.HashMap;

public class PaymentService {
    private final HashMap<String, Ride> rideDetails;
    private final HashMap<String, Rider> riderDetails;

    public PaymentService(HashMap<String, Ride> rideDetails, HashMap<String, Rider> riderDetails) {
        this.rideDetails = rideDetails;
        this.riderDetails = riderDetails;
    }

    public void payViaWallet(String rideID) {
        Ride currentRide = rideDetails.get(rideID);
        Rider rider = riderDetails.get(currentRide.riderID);

        rider.deductMoney(currentRide.bill);
    }

    public void addMoney(String riderID, float amount) {
        Rider rider = riderDetails.get(riderID);

        rider.addMoney(amount);
    }
}
