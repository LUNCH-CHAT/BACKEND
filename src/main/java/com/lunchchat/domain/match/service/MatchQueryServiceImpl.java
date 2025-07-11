package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.converter.MatchConverter;
import com.lunchchat.domain.match.dto.MatchResponseDto.MatchListDto;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.match.repository.MatchRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.MatchHandler;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchQueryServiceImpl implements MatchQueryService {
  private final MatchRepository matchRepository;

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

      default -> throw new MatchHandler(ErrorStatus.INVALID_MATCH_STATUS);
    }

    return matches;
  }

  @Override
  public List<MatchListDto> getMatchListDtosByStatus(MatchStatusType status, Long memberId) {
    List<Matches> matchList = getMatchesByStatus(status, memberId);

    return matchList.stream()
        .map(match -> {
          Member opponent = getOpponent(memberId, match);
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