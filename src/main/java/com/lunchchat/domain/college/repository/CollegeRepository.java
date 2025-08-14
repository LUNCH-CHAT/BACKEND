package com.lunchchat.domain.college.repository;

import com.lunchchat.domain.college.entity.College;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {
  Optional<College> findById(Long id);
  List<College> findByUniversityId(Long universityId);

}

