package com.lunchchat.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;

public class MemberRequestDTO {
  @Getter
  public static class UpdateInterestDTO {

    @NotNull
    @Size(max = 3, message = "최대 3개의 관심사를 선택할 수 있습니다.")
    List<Long> interestIds;
  }
}
