package com.lunchchat.domain.time_table.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.time_table.dto.TimeTableUpdateRequestDTO;
import com.lunchchat.domain.time_table.entity.TimeTable;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimeTableCommandServiceImpl implements TimeTableCommandService {

  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public void updateTimeTable(String email, TimeTableUpdateRequestDTO request) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

    // 기존 시간표 삭제
    member.getTimeTables().clear();

    // 새로운 시간표 추가
    List<TimeTable> newTimeTables = request.timeTableList().stream()
        .map(dto -> TimeTable.create(
            dto.dayOfWeek(),
            dto.startTime(),
            dto.endTime(),
            dto.subjectName()
        ))
        .collect(Collectors.toList());

    member.addTimeTables(newTimeTables);
  }

}
