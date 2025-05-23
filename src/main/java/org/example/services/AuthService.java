package org.example.services;

import org.example.dto.CompleteProfileRequest;
import org.example.dto.LoginRequest;
import org.example.dto.JwtResponse;
import org.example.dto.RegisterRequest;

public interface AuthService {
    JwtResponse loginUser(LoginRequest req);
    boolean registerUser(RegisterRequest req);
    boolean completeUserProfile(CompleteProfileRequest req);
}
