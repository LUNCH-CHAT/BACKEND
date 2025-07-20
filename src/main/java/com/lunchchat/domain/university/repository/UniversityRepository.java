package com.lunchchat.domain.university.repository;

import com.lunchchat.domain.university.entity.University;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository {
  Optional<University> findByDomain(String Domain);
}
