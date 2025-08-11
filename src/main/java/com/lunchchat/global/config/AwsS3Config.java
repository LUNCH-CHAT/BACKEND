package com.lunchchat.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class AwsS3Config {

  private final AwsS3Properties properties;

  @Bean
  public S3Presigner s3Presigner() {
    return S3Presigner.builder()
        .region(Region.of(properties.getRegion()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }
}