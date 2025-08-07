package com.lunchchat;

import com.lunchchat.global.config.AwsS3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(AwsS3Properties.class)
public class LaunchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaunchatApplication.class, args);
    }

}
