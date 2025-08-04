package com.lunchchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LaunchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaunchatApplication.class, args);
    }

}
