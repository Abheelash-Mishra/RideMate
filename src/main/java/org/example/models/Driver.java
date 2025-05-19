package org.example.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("DRIVER")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int x_coordinate;
    private int y_coordinate;

    private String phoneNumber;
    private String address;
    private boolean available = true;
    private float earnings = 0;
    private float rating = 0;
    private float ratingSum = 0;
    private int ridesDone = 0;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Driver(String phoneNumber, String address, int x_coordinate, int y_coordinate) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
    }
}
