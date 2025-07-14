package com.lunchchat.domain.user_statistics.service;

public interface UserStatisticsCommandService {
  void incrementRequestedCount(Long memberId);
  void incrementReceivedCount(Long memberId);
  void incrementAcceptedCount(Long memberId);
}
