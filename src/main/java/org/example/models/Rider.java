package org.example.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Rider {
    @Id
    private long riderID;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> coordinates;

    private float walletAmount = 0;
    private List<Long> matchedDrivers;

    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Rider(long riderID, int x_coordinate, int y_coordinate) {
        this.riderID = riderID;
        this.coordinates = List.of(x_coordinate, y_coordinate);
    }
}
