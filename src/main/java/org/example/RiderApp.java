package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCaching
public class RiderApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RiderApp.class, args);

//        CLI mode is disabled until it has been updated to work with authentication
//        if (args.length > 0 && "cli".equalsIgnoreCase(args[0])) {
//            RiderAppCLI riderAppCLI = context.getBean(RiderAppCLI.class);
//            riderAppCLI.start();
//            context.close();
//        }
    }
}

