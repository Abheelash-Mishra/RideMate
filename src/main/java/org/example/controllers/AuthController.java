package org.example.controllers;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CompleteProfileRequest;
import org.example.dto.LoginRequest;
import org.example.dto.JwtResponse;
import org.example.dto.RegisterRequest;
import org.example.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        log.info("Accessing endpoint: /auth/login");

        return ResponseEntity.ok(authService.loginUser(request));
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Boolean> registerUser(@RequestBody RegisterRequest request) {
        log.info("Accessing endpoint: /auth/register");

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(request));
    }

    @PostMapping("/profile")
    public ResponseEntity<Boolean> completeProfile(@RequestBody CompleteProfileRequest request) {
        log.info("Accessing endpoint: /auth/register");

        return ResponseEntity.ok().body(authService.completeUserProfile(request));
    }
}
