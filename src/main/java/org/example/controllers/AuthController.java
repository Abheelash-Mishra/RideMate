package org.example.controllers;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.LoginRequest;
import org.example.dto.JwtResponse;
import org.example.dto.RegisterRequest;
import org.example.models.Driver;
import org.example.models.Rider;
import org.example.models.Role;
import org.example.models.User;
import org.example.repository.DriverRepository;
import org.example.repository.RiderRepository;
import org.example.repository.UserRepository;
import org.example.services.impl.UserDetailsImpl;
import org.example.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Accessing endpoint: /auth/login");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getRole()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register/rider")
    @Transactional
    public ResponseEntity<?> registerRider(@RequestBody RegisterRequest request) {
        log.info("Accessing endpoint: /auth/register/rider");

        return registerUser(request, Role.RIDER);
    }

    @PostMapping("/register/driver")
    @Transactional
    public ResponseEntity<?> registerDriver(@RequestBody RegisterRequest request) {
        log.info("Accessing endpoint: /auth/register/driver");

        return registerUser(request, Role.DRIVER);
    }

    private ResponseEntity<?> registerUser(RegisterRequest req, Role role) {
        String emailRegex = "^[a-zA-Z0-9_.±]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if (!Pattern.matches(emailRegex, req.getEmail())) {
            log.warn("Invalid email ID was used. Driver was not registered");
            return ResponseEntity.badRequest().body("Invalid email used");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            log.warn("Email is already in use.");
            return ResponseEntity.badRequest().body("Email already registered");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(role);
        User savedUser = userRepository.save(user);

        if (role == Role.RIDER) {
            Rider rider = new Rider(req.getPhoneNumber(), req.getAddress(), req.getX_coordinate(), req.getY_coordinate());
            rider.setUser(savedUser);
            riderRepository.save(rider);
        }
        else {
            Driver driver = new Driver(req.getPhoneNumber(), req.getAddress(), req.getX_coordinate(), req.getY_coordinate());
            driver.setUser(savedUser);
            driverRepository.save(driver);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
    }
}
