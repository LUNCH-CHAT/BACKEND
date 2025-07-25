package com.lunchchat.global.security.auth.dto;

public record TokenDTO() {

  public record Request(
  ){}

  public record Response(
    String accessToken,
    String refreshToken
  ){}

}
