package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.converter.MemberConverter;
import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.domain.member.util.S3PresignedUrlUtil;
import com.lunchchat.global.config.AmazonConfig;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

  private final S3PresignedUrlUtil s3Util;
  private final AmazonConfig config;

  public MemberResponseDTO.PresignedUrlResponse generateUploadUrl(String fileName) {
    String folderPrefix = config.getProfilePath();
    String sanitizedFileName = UUID.randomUUID() + "_" + fileName;
    String key = folderPrefix + "/" + sanitizedFileName;

    String presignedUrl = s3Util.generatePresignedUploadUrl(key);
    String s3Url =
        "https://" + config.getBucket() + ".s3." + config.getRegion() + ".amazonaws.com/" + key;

    return MemberConverter.toPresignedUrlResponse(presignedUrl, s3Url);
  }
}