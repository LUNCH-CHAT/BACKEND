package com.lunchchat.domain.match.repository;

import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.match.entity.Matches;
import java.util.Optional;

import com.lunchchat.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Matches, Long> {
    Page<Matches> findByStatusAndFromMemberId(MatchStatus status, Long fromMemberId, Pageable pageable);

    Page<Matches> findByStatusAndToMemberId(MatchStatus status, Long toMemberId, Pageable pageable);

    int countByToMemberAndStatus(Member toMember, MatchStatus status);

    int countByFromMemberAndStatus(Member fromMember, MatchStatus status);

    @Query("""
        SELECT m FROM Matches m
        WHERE m.status = :status
        AND (m.fromMember.id = :memberId OR m.toMember.id = :memberId)
        ORDER BY m.createdAt DESC
        """)
    Page<Matches> findByStatusAndMemberId(@Param("status") MatchStatus status, @Param("memberId") Long memberId, Pageable pageable);

    @Query("""
    SELECT m FROM Matches m
    WHERE ((m.fromMember.id = :memberId AND m.toMember.id = :toMemberId)
        OR (m.fromMember.id = :toMemberId AND m.toMember.id = :memberId))
    """)
    Optional<Matches> findActiveMatchBetween(@Param("memberId") Long memberId, @Param("toMemberId") Long toMemberId);

    @Query("""
    SELECT m FROM Matches m
    WHERE 
        (m.fromMember.email = :viewerEmail AND m.toMember.email = :memberEmail)
        OR
        (m.fromMember.email = :memberEmail AND m.toMember.email = :viewerEmail)
    """)
    Optional<Matches> findMatchStatusBetween(@Param("viewerEmail") String viewerEmail,
                                             @Param("memberEmail") String memberEmail);

    @Query("""
    SELECT COUNT(m) FROM Matches m
    WHERE (m.fromMember.email = :memberEmail OR m.toMember.email = :memberEmail)
    """)
    long countMatchesByMember(@Param("memberEmail") String memberEmail);

}
