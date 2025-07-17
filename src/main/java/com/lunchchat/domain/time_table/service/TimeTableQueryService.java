package com.lunchchat.domain.time_table.service;

import com.lunchchat.domain.time_table.entity.TimeTable;

import java.util.List;

public interface TimeTableQueryService {
    List<TimeTable> findByMemberId(Long memberId);
}
