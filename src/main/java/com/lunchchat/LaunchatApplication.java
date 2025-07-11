package com.lunchchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.lunchchat.domain")
public class LaunchatApplication {

  public static void main(String[] args) {
    SpringApplication.run(LaunchatApplication.class, args);
  }

}
