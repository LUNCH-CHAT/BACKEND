package com.lunchchat.domain.user_keywords.converter;

import com.lunchchat.domain.member.dto.MemberRequestDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.user_keywords.entity.UserKeyword;
import java.util.List;
import java.util.stream.Collectors;

public class UserKeywordsConverter {

  public static UserKeyword toEntity(Member member, MemberRequestDTO.UpdateKeywordDTO dto) {
    return UserKeyword.builder()
        .member(member)
        .type(dto.getType())
        .title(dto.getTitle())
        .description(dto.getDescription())
        .build();
  }

  public static List<UserKeyword> toEntityList(Member member, List<MemberRequestDTO.UpdateKeywordDTO> dtos) {
    return dtos.stream()
        .map(dto -> toEntity(member, dto))
        .collect(Collectors.toList());
  }
}
