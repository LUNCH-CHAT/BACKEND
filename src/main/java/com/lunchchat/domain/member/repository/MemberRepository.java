package com.lunchchat.domain.member.repository;

import com.lunchchat.domain.member.entity.Member;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  // 이메일 기준 조회
  Optional<Member> findByEmail (String email);

  // 학번 중복 조회
  boolean existsByStudentNo (String studentNo);

  Page<Member> findByIdNot(Long excludedId, Pageable pageable);

}