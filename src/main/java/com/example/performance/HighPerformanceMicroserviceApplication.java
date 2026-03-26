package com.example.performance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HighPerformanceMicroserviceApplication {

    public static void main(String[] args) {

        /*
        =====================================================
        APPLICATION STARTUP

        PERFORMANCE:
        - Uses optimized Spring Boot startup mechanism
        - Lazy initialization (optional) can reduce startup time
        =====================================================
        */
        SpringApplication app = new SpringApplication(HighPerformanceMicroserviceApplication.class);

        // Optional optimization for large applications
        // app.setLazyInitialization(true);

        app.run(args);
    }
}