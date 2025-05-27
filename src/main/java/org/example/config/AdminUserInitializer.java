package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.models.User;
import org.example.models.Role;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class AdminUserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private String adminEmail = "admin@gmail.com";
    private String adminPassword = "admin@test";

    @Autowired
    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Admin user with email {} already exists. Skipping creation.", adminEmail);
        }
        else {
            User adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(Role.ADMIN);
            userRepository.save(adminUser);

            log.info("Admin user {} created successfully with role ADMIN.", adminEmail);
        }
    }
}
