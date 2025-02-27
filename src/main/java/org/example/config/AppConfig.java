package org.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan(basePackages = {
        "org.example.services",
        "org.example.repository",
})
@Profile("cli")
public class AppConfig {
}
