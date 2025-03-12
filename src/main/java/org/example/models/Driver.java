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
    private String driverID;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> coordinates;

    private boolean available = true;
    private float earnings = 0;
    private float rating = 0;
    private float ratingSum = 0;
    private int ridesDone = 0;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Driver(String driverID, int x_coordinate, int y_coordinate) {
        this.driverID = driverID;
        this.coordinates = new ArrayList<>(List.of(x_coordinate, y_coordinate));
    }
}
