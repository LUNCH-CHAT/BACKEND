package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.converter.MemberConverter;
import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.domain.member.util.S3PresignedUrlUtil;
import com.lunchchat.global.config.AwsS3Properties;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileImageService {

  private final S3PresignedUrlUtil s3Util;
  private final AwsS3Properties awsProps;

  public MemberResponseDTO.PresignedUrlResponse generateUploadUrl(String fileName) {
    String folderPrefix = awsProps.getS3().getPath().getProfile();
    String sanitizedFileName = UUID.randomUUID() + "_" + fileName;
    String key = folderPrefix + "/" + sanitizedFileName;

    String presignedUrl = s3Util.generatePresignedUploadUrl(key);
    String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s",
        awsProps.getS3().getBucket(),
        awsProps.getRegion(),
        key
    );

    return MemberConverter.toPresignedUrlResponse(presignedUrl, s3Url);
  }
}