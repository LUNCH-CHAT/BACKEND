package com.lunchchat.domain.match.converter;

import com.lunchchat.domain.match.dto.MatchResponseDto;
import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_keywords.entity.UserKeyword;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MatchConverter {

//  public static MatchResponseDto.MatchListDto toMatchListDto(Matches match, Member opponent) {
//    return MatchResponseDto.MatchListDto.builder()
//        .id(match.getId())
//        .createdAt(match.getCreatedAt())
//        .matchedUser(toMatchedUserDto(opponent))
//        .build();
//  }

//  public static MatchResponseDto.MatchedUserDto toMatchedUserDto(Member member) {
//    return MatchResponseDto.MatchedUserDto.builder()
//        .id(member.getId())
//        .memberName(member.getMembername())
//        .studentNo(member.getStudentNo())
//        .department(member.getDepartment().getName())
////        .profileImageUrl(member.getProfileImageUrl())
//        .userKeywords(toKeywordDtoList(member.getUserKeywords()))
//        .userInterests(toInterestDtoList(member.getUserInterests()))
//        .build();
//  }

  private static List<MatchResponseDto.KeywordDto> toKeywordDtoList(List<UserKeyword> keywords) {
    return keywords.stream()
        .map(keyword -> MatchResponseDto.KeywordDto.builder()
            .id(keyword.getId())
            .keywordName(keyword.getTitle())
            .build())
        .collect(Collectors.toList());
  }

//  private static List<MatchResponseDto.InterestDto> toInterestDtoList(List<Interest> interests) {
//    return interests.stream()
//        .map(interest -> MatchResponseDto.InterestDto.builder()
//            .id(interest.getInterests().getId())
//            .interestName(interest.getInterests().getName().toString())
//            .build())
//        .collect(Collectors.toList());
//  }

  public static MatchResponseDto.MatchResultDto toMatchResultDto(Matches match) {
    return MatchResponseDto.MatchResultDto.builder()
        .id(match.getId())
        .status(match.getStatus())
        .createdAt(match.getCreatedAt())
        .build();
  }

  public static Matches toMatchEntity(Member fromMember, Member toMember) {
    return Matches.builder()
        .fromMember(fromMember)
        .toMember(toMember)
        .status(MatchStatus.REQUESTED)
        .createdAt(LocalDateTime.now())
        .build();
  }
}