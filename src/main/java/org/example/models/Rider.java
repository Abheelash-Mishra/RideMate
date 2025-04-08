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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long riderID;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> coordinates;

    private String email;
    private String phoneNumber;
    private float walletAmount = 0;
    private List<Long> matchedDrivers;

    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Rider(String email, String phoneNumber, int x_coordinate, int y_coordinate) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.coordinates = List.of(x_coordinate, y_coordinate);
    }
}
