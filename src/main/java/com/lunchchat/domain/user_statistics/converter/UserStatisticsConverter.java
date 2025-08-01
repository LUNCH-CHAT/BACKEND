package com.lunchchat.domain.user_statistics.converter;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.user_statistics.entity.UserStatistics;

public class UserStatisticsConverter {
  public static UserStatistics toUserStatistics(Member member) {
    return UserStatistics.builder()
        .member(member)
        .matchRequestedCount(0)
        .matchReceivedCount(0)
        .matchCompletedCount(0)
        .build();
  }

}
