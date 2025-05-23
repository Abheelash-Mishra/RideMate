package org.example.services.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CompleteProfileRequest;
import org.example.dto.JwtResponse;
import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.exceptions.EmailAlreadyExistsException;
import org.example.exceptions.InvalidEmailException;
import org.example.exceptions.InvalidRoleException;
import org.example.exceptions.InvalidUserException;
import org.example.models.Driver;
import org.example.models.Rider;
import org.example.models.Role;
import org.example.models.User;
import org.example.repository.DriverRepository;
import org.example.repository.RiderRepository;
import org.example.repository.UserRepository;
import org.example.services.AuthService;
import org.example.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
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

    @Override
    public JwtResponse loginUser(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            return new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getRole()
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("User could not be logged in");
        }
    }

    @Override
    @Transactional
    public boolean registerUser(RegisterRequest req) {
        String emailRegex = "^[a-zA-Z0-9_.±]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if (!Pattern.matches(emailRegex, req.getEmail())) {
            throw new InvalidEmailException("Email ID received is invalid");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException("Email ID already exists");
        }

        Role role;
        if (req.getRole().equalsIgnoreCase("Rider")) {
            role = Role.RIDER;
        }
        else if (req.getRole().equalsIgnoreCase("Driver")) {
            role = Role.DRIVER;
        }
        else {
            throw new InvalidRoleException("Received an unsupported role");
        }

        try {
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
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition caught for email {}. The 'exists' check passed, but the database insert failed with a unique constraint violation.", req.getEmail());

            throw new EmailAlreadyExistsException("Email already in use: " + req.getEmail());
        }

        return true;
    }

    @Override
    public boolean completeUserProfile(CompleteProfileRequest req) {
        User user = userRepository.findById(req.getUserID())
                .orElseThrow(() -> new InvalidUserException("User not found"));

        if (req.getRole().equalsIgnoreCase("RIDER")) {
            Rider rider = new Rider(req.getPhoneNumber(), req.getAddress(), req.getX_coordinate(), req.getY_coordinate());
            rider.setUser(user);
            riderRepository.save(rider);

            user.setRole(Role.RIDER);
            userRepository.save(user);
        }
        else if (req.getRole().equalsIgnoreCase("DRIVER")) {
            Driver driver = new Driver(req.getPhoneNumber(), req.getAddress(), req.getX_coordinate(), req.getY_coordinate());
            driver.setUser(user);
            driverRepository.save(driver);

            user.setRole(Role.DRIVER);
            userRepository.save(user);
        }
        else {
            log.warn("Invalid role received in request");

            throw new InvalidRoleException("Received an unsupported role");
        }

        return true;
    }
}
