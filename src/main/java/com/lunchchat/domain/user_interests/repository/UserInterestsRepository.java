package com.lunchchat.domain.user_interests.repository;

import com.lunchchat.domain.user_interests.entity.Interest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInterestsRepository extends JpaRepository<Interest, Long> {
//  @Query("SELECT ui.interests.name FROM Interest ui WHERE ui.user.id = :memberId")
//  List<String> findInterestNamesByMemberId(@Param("memberId") Long memberId);
}
