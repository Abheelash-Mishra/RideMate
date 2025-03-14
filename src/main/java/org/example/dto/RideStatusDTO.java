package org.example.dto;

import org.example.models.RideStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideStatusDTO {
    private long rideID;
    private long riderID;
    private long driverID;
    private RideStatus status;
}