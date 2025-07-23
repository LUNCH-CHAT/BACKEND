package com.lunchchat.global.security.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lunchchat.domain.member.entity.enums.InterestType;
import com.lunchchat.domain.time_table.dto.TimeTableDTO;
import java.util.List;
import java.util.Set;

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

  // 런치챗 내부 로그인
  public record SingUpRequest(
      String membername,
      String studentNo,
      Long collegeId,
      Long departmentId,
      Set<InterestType> interests,
      List<TimeTableDTO> timeTables
  ) {}
}
