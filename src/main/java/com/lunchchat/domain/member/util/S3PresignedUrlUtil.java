package com.lunchchat.domain.member.util;

import com.lunchchat.global.config.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URL;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3PresignedUrlUtil {

  private final S3Presigner s3Presigner;
  private final AwsS3Properties properties;

  public String generatePresignedUploadUrl(String key) {
    PutObjectRequest objectRequest = PutObjectRequest.builder()
        .bucket(properties.getS3().getBucket())
        .key(key)
        .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(5))
        .putObjectRequest(objectRequest)
        .build();

    URL url = s3Presigner.presignPutObject(presignRequest).url();
    return url.toString();
  }
}