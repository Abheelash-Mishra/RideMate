package org.example.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("RIDER")
public class Rider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private int x_coordinate;
    private int y_coordinate;

    private String phoneNumber;
    private String address;
    private float walletAmount = 0;
    private List<Long> matchedDrivers;

    @OneToMany(mappedBy = "rider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ride> rides = new ArrayList<>();

    public Rider(String phoneNumber, String address, int x_coordinate, int y_coordinate) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
    }
}
