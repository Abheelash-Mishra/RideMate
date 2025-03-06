package org.example.dto;

import org.example.models.RideStatus;

public record RideStatusDTO(String rideID, String riderID, String driverID, RideStatus status) {}