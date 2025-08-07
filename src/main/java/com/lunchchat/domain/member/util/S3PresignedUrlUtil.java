package com.lunchchat.domain.member.util;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.lunchchat.global.config.AmazonConfig;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3PresignedUrlUtil {
  private final AmazonS3 amazonS3;
  private final AmazonConfig amazonConfig;

  public String generatePresignedUploadUrl(String key) {
    Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5); // 5분

    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
        amazonConfig.getBucket(), key)
        .withMethod(HttpMethod.PUT)
        .withExpiration(expiration);

    return amazonS3.generatePresignedUrl(request).toString();
  }

  public String getObjectUrl(String key) {
    return amazonS3.getUrl(amazonConfig.getBucket(), key).toString();
  }
}