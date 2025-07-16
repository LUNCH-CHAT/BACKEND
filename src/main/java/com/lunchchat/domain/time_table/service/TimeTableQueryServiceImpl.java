package com.lunchchat.domain.time_table.service;

import com.lunchchat.domain.time_table.entity.TimeTable;
import com.lunchchat.domain.time_table.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeTableQueryServiceImpl implements TimeTableQueryService {

    private final TimeTableRepository timeTableRepository;

    @Override
    public List<TimeTable> findByMemberId(Long memberId) {
        return timeTableRepository.findByMemberId(memberId);
    }
}

