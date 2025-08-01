package com.lunchchat.domain.match.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MatchRequestDto {
  
  @Getter
  @NoArgsConstructor
  public static class CreateMatchDto {
    @Schema(description = "매칭 대상 사용자 ID", required = true)
    private Long toMemberId;
  }
  
  @Getter
  @NoArgsConstructor
  public static class CreateMatchRequest {
    @Schema(description = "매칭 대상 사용자 ID", required = true)
    private Long toMemberId;
  }
}
