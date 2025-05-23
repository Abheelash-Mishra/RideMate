package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteProfileRequest {
    private long userID;
    private String phoneNumber;
    private String address;
    private int x_coordinate;
    private int y_coordinate;
    private String role;
}
