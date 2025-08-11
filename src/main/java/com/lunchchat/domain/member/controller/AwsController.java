package com.lunchchat.domain.member.controller;

import com.lunchchat.domain.member.dto.MemberRequestDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO.PresignedUrlResponse;
import com.lunchchat.domain.member.service.ProfileImageService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aws")
public class AwsController {

  private final ProfileImageService profileImageService;

  @PostMapping("/presigned-url")
  @Operation(summary = "프로필 presigned URL 발급", description = "파일명을 받아 S3 업로드용 presigned URL을 발급합니다.")
  public ApiResponse<PresignedUrlResponse> getPresignedUrl(
      @RequestBody @Valid MemberRequestDTO.PresignedUrlRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    PresignedUrlResponse response = profileImageService.generateUploadUrl(request.getFileName());
    return ApiResponse.onSuccess(response);
  }

}
