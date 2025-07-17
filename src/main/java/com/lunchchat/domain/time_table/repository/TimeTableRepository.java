package com.lunchchat.domain.time_table.repository;

import com.lunchchat.domain.time_table.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    List<TimeTable> findByMemberId(Long memberId);
}
