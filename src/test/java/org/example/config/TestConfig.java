package org.example.config;

import org.example.repository.Database;
import org.example.services.admin.AdminService;
import org.example.services.admin.AdminServiceImpl;
import org.example.services.driver.DriverService;
import org.example.services.driver.DriverServiceImpl;
import org.example.services.payment.PaymentService;
import org.example.services.ride.RideService;
import org.example.services.ride.RideServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestConfig {
    @Bean
    public Database mockDatabase() {
        return mock(Database.class);
    }

    @Bean
    public AdminService adminService(Database database) {
        return new AdminServiceImpl(database);
    }

    @Bean
    public DriverService driverService(Database database) {
        return new DriverServiceImpl(database);
    }

    @Bean
    public RideService rideService(Database database) {
        return new RideServiceImpl(database);
    }

    @Bean
    public PaymentService paymentService(Database database) {
        return new PaymentService(database);
    }
}
