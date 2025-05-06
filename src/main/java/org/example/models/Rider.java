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

    private int x_coordinate;
    private int y_coordinate;

    private String email;
    private String phoneNumber;
    private float walletAmount = 0;

    @Column(name = "matched_drivers", columnDefinition = "VARBINARY(1024)")
    private List<Long> matchedDrivers;

    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Rider(String email, String phoneNumber, int x_coordinate, int y_coordinate) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
    }
}
