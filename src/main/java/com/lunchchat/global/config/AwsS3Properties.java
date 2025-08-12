package com.lunchchat.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsS3Properties {
  private String region;
  private S3 s3;

  @Getter @Setter
  public static class S3 {
    private String bucket;
    private Path path;

    @Getter @Setter
    public static class Path {
      private String profile;
    }
  }
}