package com.lunchchat.domain.user_interests.repository;

import com.lunchchat.domain.member.entity.enums.InterestType;
import com.lunchchat.domain.user_interests.entity.Interest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {
//  @Query("SELECT ui.interests.name FROM Interest ui WHERE ui.user.id = :memberId")
//  List<String> findInterestNamesByMemberId(@Param("memberId") Long memberId);

  // 관심사 정합성 확인
  Optional<Interest> findByType(InterestType type);
}
