package com.lunchchat.domain.match.converter;

import com.lunchchat.domain.match.dto.MatchResponseDto;
import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_keywords.entity.UserKeyword;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class MatchConverter {

  public static MatchResponseDto.MatchListDto toMatchListDto(Matches match, Member opponent) {
    return new MatchResponseDto.MatchListDto(
        match.getId(),
        match.getCreatedAt(),
        toMatchedUserDto(opponent)
    );
  }


  public static MatchResponseDto.MatchListPageDto toMatchListPageDto(Page<Matches> matchPage, Long currentUserId) {
    List<MatchResponseDto.MatchListDto> matchList = matchPage.stream()
        .map(match -> {
          Member opponent = getOpponent(currentUserId, match);
          return toMatchListDto(match, opponent);
        })
        .toList();

    return new MatchResponseDto.MatchListPageDto(
        matchList,
        matchList.size(),
        matchPage.getTotalPages(),
        matchPage.getTotalElements(),
        matchPage.isFirst(),
        matchPage.hasNext()
    );
  }

  private static MatchResponseDto.MatchedUserDto toMatchedUserDto(Member member) {
    return new MatchResponseDto.MatchedUserDto(
        member.getId(),
        member.getMembername(),
        member.getStudentNo(),
        member.getDepartment().getName(),
        member.getProfileImageUrl(),
        toKeywordDtoList(member.getUserKeywords()),
        toInterestDtoList(member.getInterests())
    );
  }

  private static Member getOpponent(Long currentUserId, Matches match) {
    return match.getFromMember().getId().equals(currentUserId)
        ? match.getToMember()
        : match.getFromMember();
  }

  private static List<MatchResponseDto.KeywordDto> toKeywordDtoList(List<UserKeyword> keywords) {
    return keywords.stream()
        .map(k -> new MatchResponseDto.KeywordDto(k.getId(), k.getTitle()))
        .collect(Collectors.toList());
  }

  private static List<MatchResponseDto.InterestDto> toInterestDtoList(Set<Interest> interests) {
    return interests.stream()
        .map(i -> new MatchResponseDto.InterestDto(i.getId(), i.getType()))
        .collect(Collectors.toList());
  }

  public static MatchResponseDto.MatchResultDto toMatchResultDto(Matches match) {
    return new MatchResponseDto.MatchResultDto(
        match.getId(),
        match.getStatus(),
        match.getCreatedAt()
    );
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