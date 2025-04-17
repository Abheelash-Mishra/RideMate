package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideDetailsDTO {
    private long rideID;
    private long driverID;
    private String destination;
    private float billAmount;
    private int rideDuration;
}
