package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.converter.MatchConverter;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.match.repository.MatchRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.MemberHandler;
import jakarta.transaction.Transactional;
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
    Member fromMember = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberHandler(ErrorStatus.USER_NOT_FOUND));

    Member toMember = memberRepository.findById(toMemberId)
        .orElseThrow(() -> new MemberHandler(ErrorStatus.USER_NOT_FOUND));

    Matches newMatch = MatchConverter.toMatchEntity(fromMember, toMember);
    return matchRepository.save(newMatch);
  }

}
