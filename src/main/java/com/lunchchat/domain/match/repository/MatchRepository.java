package com.lunchchat.domain.match.repository;

import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.entity.Matches;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Matches, Long> {
    List<Matches> findByStatusAndFromMemberId(MatchStatus status, Long fromMemberId);

    List<Matches> findByStatusAndToMemberId(MatchStatus status, Long toMemberId);

    @Query("""
        SELECT m FROM Matches m
        WHERE m.status = :status
        AND (m.fromMember.id = :memberId OR m.toMember.id = :memberId)
        ORDER BY m.createdAt DESC
        """)
    List<Matches> findByStatusAndMemberId(@Param("status") MatchStatus status, @Param("memberId") Long memberId);
}
