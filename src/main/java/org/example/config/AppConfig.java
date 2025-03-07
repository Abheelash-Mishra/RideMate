package org.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "org.example.services",
        "org.example.repository",
})
public class AppConfig {
}
