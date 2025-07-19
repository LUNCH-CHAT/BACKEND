package com.lunchchat.domain.user_keywords.repository;

import com.lunchchat.domain.user_keywords.entity.UserKeyword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserKeywordsRepository extends JpaRepository<UserKeyword, Long> {
  @Query("SELECT uk.title FROM UserKeyword uk WHERE uk.member.id = :memberId")
  List<String> findTitlesByMemberId(@Param("memberId") Long memberId);
}
