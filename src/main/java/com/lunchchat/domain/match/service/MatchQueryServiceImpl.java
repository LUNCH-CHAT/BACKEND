package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.converter.MatchConverter;
import com.lunchchat.domain.match.dto.MatchResponseDto.MatchListDto;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.match.repository.MatchRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.MatchException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchQueryServiceImpl implements MatchQueryService {
  private final MatchRepository matchRepository;
  private final MemberRepository memberRepository;

  @Override
  public List<Matches> getMatchesByStatus(MatchStatusType status, Long memberId) {
    List<Matches> matches;

    switch (status) {
      case ACCEPTED ->
          matches = matchRepository.findByStatusAndMemberId(MatchStatus.ACCEPTED, memberId);

      case REQUESTED ->
          matches = matchRepository.findByStatusAndFromMemberId(MatchStatus.REQUESTED, memberId);

      case RECEIVED ->
          matches = matchRepository.findByStatusAndToMemberId(MatchStatus.REQUESTED, memberId);

      default -> throw new MatchException(ErrorStatus.INVALID_MATCH_STATUS);
    }

    return matches;
  }

  @Override
  public List<MatchListDto> getMatchListDtosByStatus(MatchStatusType status, String email) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MatchException(ErrorStatus.USER_NOT_FOUND));

    List<Matches> matchList = getMatchesByStatus(status, member.getId());

    return matchList.stream()
        .map(match -> {
          Member opponent = getOpponent(member.getId(), match);
          return MatchConverter.toMatchListDto(match, opponent);
        })
        .collect(Collectors.toList());
  }

  private Member getOpponent(Long currentUserId, Matches match) {
    if (match.getFromMember().getId().equals(currentUserId)) {
      return match.getToMember();
    } else {
      return match.getFromMember();
    }
  }
}