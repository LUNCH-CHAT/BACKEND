package com.lunchchat.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record KeywordRecommendationDTO(){

  public record request(
    @NotBlank
    String description
  ){}

  public record response(
    @NotBlank
    String keyword
  ){}
}
