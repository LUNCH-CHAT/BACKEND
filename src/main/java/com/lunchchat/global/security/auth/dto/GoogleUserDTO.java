package com.lunchchat.global.security.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserDTO(){

  public record Request(
      String code
  ) {}

  public record Response(
      String email,
      String name
  ) {}

  // 구글 토큰 응답
  public record TokenResponse(
      @JsonProperty("access_token")
      String accessToken
  ) {}

  // 구글 프로필 응답
  public record ProfileResponse(
      String id,
      String email,
      String name
  ) {}
}
