package org.example.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Rider {
    @Id
    private String riderID;
    private int[] coordinates;
    private float walletAmount = 0;
    private List<String> matchedDrivers;

    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Rider(String riderID, int x_coordinate, int y_coordinate) {
        this.riderID = riderID;
        this.coordinates = new int[]{x_coordinate, y_coordinate};
    }
}
