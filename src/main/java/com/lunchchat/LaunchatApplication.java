package com.lunchchat;

import com.lunchchat.global.config.AwsS3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
@EnableScheduling
@EnableConfigurationProperties(AwsS3Properties.class)
@EnableJpaRepositories(
    basePackages = "com.lunchchat.domain",
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MongoRepository.class)
)
@EnableMongoRepositories(
    basePackages = "com.lunchchat.domain",
    includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = MongoRepository.class)
)
public class LaunchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaunchatApplication.class, args);
    }

}
