package com.lunchchat.domain.match.service;

import com.lunchchat.domain.match.entity.Matches;

public interface MatchCommandService {
  Matches requestMatch(String senderEmail, Long toMemberId);
  void acceptMatch(Long matchId, String memberEmail);
}
