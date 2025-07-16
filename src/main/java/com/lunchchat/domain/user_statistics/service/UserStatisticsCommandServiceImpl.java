package com.lunchchat.domain.user_statistics.service;

import com.lunchchat.domain.user_statistics.entity.UserStatistics;
import com.lunchchat.domain.user_statistics.repository.UserStatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatisticsCommandServiceImpl implements UserStatisticsCommandService {
  private final UserStatisticsRepository userStatisticsRepository;

  // TODO: 회원가입 구현 후 UserStatistics 자동 생성 로직 추가

  @Override
  @Transactional
  public void incrementRequestedCount(Long memberId) {
    UserStatistics userStatistics = userStatisticsRepository.findByMemberId(memberId);
    userStatistics.incrementRequested();
    userStatisticsRepository.save(userStatistics);
  }

  @Override
  @Transactional
  public void incrementReceivedCount(Long memberId) {
    UserStatistics userStatistics = userStatisticsRepository.findByMemberId(memberId);
    userStatistics.incrementReceived();
    userStatisticsRepository.save(userStatistics);
  }

  @Override
  @Transactional
  public void incrementAcceptedCount(Long memberId) {
    UserStatistics userStatistics = userStatisticsRepository.findByMemberId(memberId);
    userStatistics.incrementCompleted();
    userStatisticsRepository.save(userStatistics);
  }

}
