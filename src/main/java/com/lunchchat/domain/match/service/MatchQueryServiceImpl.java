package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.converter.MatchConverter;
import com.lunchchat.domain.match.dto.MatchResponseDto;
import com.lunchchat.domain.match.dto.MatchResponseDto.MatchListDto;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.match.repository.MatchRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.PaginatedResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.MatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchQueryServiceImpl implements MatchQueryService {
  private final MatchRepository matchRepository;
  private final MemberRepository memberRepository;

  @Override
  public Page<Matches> getMatchesByStatus(MatchStatusType status, Long memberId, PageRequest pageable) {
    Page<Matches> matches;

    switch (status) {
      case ACCEPTED ->
          matches = matchRepository.findByStatusAndMemberId(MatchStatus.ACCEPTED, memberId, pageable);

      case REQUESTED ->
          matches = matchRepository.findByStatusAndFromMemberId(MatchStatus.REQUESTED, memberId, pageable);

      case RECEIVED ->
          matches = matchRepository.findByStatusAndToMemberId(MatchStatus.REQUESTED, memberId, pageable);

      default -> throw new MatchException(ErrorStatus.INVALID_MATCH_STATUS);
    }

    return matches;
  }

  @Override
  @Transactional(readOnly = true)
  public PaginatedResponse<MatchListDto> getMatchListDtosByStatus(MatchStatusType status, String email, int page, int size) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MatchException(ErrorStatus.USER_NOT_FOUND));

    PageRequest pageable = PageRequest.of(page, size);
    Page<Matches> matchPage = getMatchesByStatus(status, member.getId(), pageable);

    return MatchConverter.toPaginatedMatchListDto(matchPage, member.getId());
  }
}