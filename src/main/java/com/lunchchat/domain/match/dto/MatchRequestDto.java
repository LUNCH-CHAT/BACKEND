package com.lunchchat.domain.match.dto;

import lombok.Getter;

public class MatchRequestDto {
  @Getter
  public static class CreateMatchDto {
    private Long toMemberId;
  }
}
