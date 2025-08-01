package com.lunchchat.domain.user_statistics.service;

import com.lunchchat.domain.member.entity.Member;

public interface UserStatisticsCommandService {
  void incrementRequestedCount(Member member);
  void incrementReceivedCount(Member member);
  void incrementAcceptedCount(Member member);
}