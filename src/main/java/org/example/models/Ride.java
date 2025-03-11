package org.example.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Ride {
    @Id
    private String rideID;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    private int[] destinationCoordinates;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    private int timeTakenInMins;
    private float bill;

    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    public Ride(String rideID, Rider rider, Driver driver) {
        this.rideID = rideID;
        this.rider = rider;
        this.driver = driver;
        this.status = RideStatus.STARTED;
    }
}

