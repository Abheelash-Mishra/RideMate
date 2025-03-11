package org.example.dto;

import org.example.models.RideStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideStatusDTO {
    private String rideID;
    private String riderID;
    private String driverID;
    private RideStatus status;
}