package org.example.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long driverID;

    private int x_coordinate;
    private int y_coordinate;

    private String email;
    private String phoneNumber;
    private boolean available = true;
    private float earnings = 0;
    private float rating = 0;
    private float ratingSum = 0;
    private int ridesDone = 0;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Driver(String email, String phoneNumber, int x_coordinate, int y_coordinate) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
    }
}
