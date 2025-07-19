package com.lunchchat.domain.user_interests.repository;

import com.lunchchat.domain.user_interests.entity.UserInterests;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInterestsRepository extends JpaRepository<UserInterests, Long> {
  @Query("SELECT ui.interests.name FROM UserInterests ui WHERE ui.user.id = :memberId")
  List<String> findInterestNamesByMemberId(@Param("memberId") Long memberId);
}
