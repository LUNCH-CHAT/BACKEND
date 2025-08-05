package com.lunchchat.domain.time_table.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.time_table.dto.TimeTableUpdateRequestDTO;
import com.lunchchat.domain.time_table.entity.TimeTable;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.TimeTableException;
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

    member.getTimeTables().clear();

    List<TimeTable> newTimeTables = request.timeTableList().stream()
        .map(dto -> {
          if (dto.startTime() == null || dto.endTime() == null) {
            throw new TimeTableException(ErrorStatus.INVALID_TIME_FORMAT);
          }

          if (!dto.startTime().isBefore(dto.endTime())) {
            throw new TimeTableException(ErrorStatus.INVALID_TIME_RANGE);
          }

          return TimeTable.create(
              dto.dayOfWeek(),
              dto.startTime(),
              dto.endTime(),
              dto.subjectName()
          );
        })
        .collect(Collectors.toList());

    member.addTimeTables(newTimeTables);
  }

}
