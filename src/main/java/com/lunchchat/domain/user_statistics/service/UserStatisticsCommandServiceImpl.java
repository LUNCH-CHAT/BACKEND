package com.lunchchat.domain.user_statistics.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.user_statistics.converter.UserStatisticsConverter;
import com.lunchchat.domain.user_statistics.entity.UserStatistics;
import com.lunchchat.domain.user_statistics.repository.UserStatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatisticsCommandServiceImpl implements UserStatisticsCommandService {
  private final UserStatisticsRepository userStatisticsRepository;

  private UserStatistics getOrCreateUserStatistics(Member member) {
    return userStatisticsRepository.findByMemberId(member.getId())
        .orElseGet(() -> {
          UserStatistics newStats = UserStatisticsConverter.toUserStatistics(member);
          return userStatisticsRepository.save(newStats);
        });
  }

  @Override
  @Transactional
  public void incrementRequestedCount(Member member) {
    UserStatistics stats = getOrCreateUserStatistics(member);
    stats.incrementRequested();
  }

  @Override
  @Transactional
  public void incrementReceivedCount(Member member) {
    UserStatistics stats = getOrCreateUserStatistics(member);
    stats.incrementReceived();
  }

  @Override
  @Transactional
  public void incrementAcceptedCount(Member member) {
    UserStatistics stats = getOrCreateUserStatistics(member);
    stats.incrementCompleted();
  }
}