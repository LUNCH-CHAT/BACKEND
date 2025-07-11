package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.entity.Matches;

public interface MatchCommandService {
  Matches requestMatch(Long memberId, Long toMemberId);
}
