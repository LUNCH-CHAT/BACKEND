package com.lunchchat.domain.user_interests.repository;

import com.lunchchat.domain.member.entity.enums.InterestType;
import com.lunchchat.domain.user_interests.entity.Interest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterestRepository extends JpaRepository<Interest, Long> {
  @Query("SELECT i.type FROM Member m JOIN m.interests i WHERE m.id = :memberId")
  List<InterestType> findInterestTypesByMemberId(@Param("memberId") Long memberId);

  // 관심사 정합성 확인
  Optional<Interest> findByType(InterestType type);
}
