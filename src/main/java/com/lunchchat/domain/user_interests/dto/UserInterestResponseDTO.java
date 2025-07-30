package com.lunchchat.domain.user_interests.dto;

import com.lunchchat.domain.member.entity.enums.InterestType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserInterestResponseDTO {
  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserInterestListDTO {
    private List<UserInterestPreviewDTO> interests;
  }

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserInterestPreviewDTO {
    private Long id;
    private InterestType interestType;
  }

}