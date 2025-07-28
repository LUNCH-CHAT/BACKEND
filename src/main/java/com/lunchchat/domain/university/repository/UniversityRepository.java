package com.lunchchat.domain.university.repository;

import com.lunchchat.domain.university.entity.University;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
  Optional<University> findByDomain(String Domain);
  Optional<University> findByName(String name);
}
