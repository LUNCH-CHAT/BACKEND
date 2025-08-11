package com.lunchchat.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record KeywordRecommendationDTO(){

  public record request(
    @NotBlank(message = "설명은 비어 있을 수 없습니다.")
    String description
  ){}

  public record response(
    @NotBlank(message = "키워드는 비어 있을 수 없습니다.")
    String keyword
  ){}
}
