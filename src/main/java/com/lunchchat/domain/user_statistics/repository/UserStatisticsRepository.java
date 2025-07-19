package com.lunchchat.domain.user_statistics.repository;

import com.lunchchat.domain.user_statistics.entity.UserStatistics;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {
    Optional<UserStatistics> findByMemberId(Long memberId);
}
