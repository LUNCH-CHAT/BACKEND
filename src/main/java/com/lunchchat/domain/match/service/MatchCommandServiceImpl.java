package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.converter.MatchConverter;
import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.match.repository.MatchRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.MatchException;
import com.lunchchat.global.apiPayLoad.exception.handler.MemberHandler;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchCommandServiceImpl implements MatchCommandService {
  private final MatchRepository matchRepository;
  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public Matches requestMatch(Long memberId, Long toMemberId) {
    if (memberId.equals(toMemberId)) {
      throw new MatchException(ErrorStatus.SELF_MATCH_REQUEST);
    }

    Optional<Matches> existingMatch = matchRepository.findActiveMatchBetween(memberId, toMemberId);
    if (existingMatch.isPresent()) {
      throw new MatchException(ErrorStatus.ALREADY_MATCHED);
    }

    Member fromMember = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberHandler(ErrorStatus.USER_NOT_FOUND));

    Member toMember = memberRepository.findById(toMemberId)
        .orElseThrow(() -> new MemberHandler(ErrorStatus.USER_NOT_FOUND));

    Matches newMatch = MatchConverter.toMatchEntity(fromMember, toMember);
    return matchRepository.save(newMatch);
  }

  @Override
  @Transactional
  public void acceptMatch(Long matchId, Long memberId) {
    Matches match = matchRepository.findById(matchId)
        .orElseThrow(() -> new MatchException(ErrorStatus.MATCH_NOT_FOUND));

    if (match.getStatus() != MatchStatus.REQUESTED) {
      throw new MatchException(ErrorStatus.INVALID_MATCH_STATUS);
    }

    match.updateStatus(MatchStatus.ACCEPTED);
    matchRepository.save(match);
  }

  @Override
  @Transactional
  public void rejectMatch(Long matchId, Long memberId) {
    Matches match = matchRepository.findById(matchId)
        .orElseThrow(() -> new MatchException(ErrorStatus.MATCH_NOT_FOUND));

    if (match.getStatus() != MatchStatus.REQUESTED) {
      throw new MatchException(ErrorStatus.INVALID_MATCH_STATUS);
    }

    match.updateStatus(MatchStatus.REJECTED);
    matchRepository.save(match);
  }
}
