package com.devtraining.systemdesign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "dateTimeProvider")
@SpringBootApplication
public class SystemDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemDesignApplication.class, args);
    }
}
