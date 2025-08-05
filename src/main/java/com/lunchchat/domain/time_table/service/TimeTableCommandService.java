package com.lunchchat.domain.time_table.service;

import com.lunchchat.domain.time_table.dto.TimeTableUpdateRequestDTO;

public interface TimeTableCommandService {
  void updateTimeTable(String email, TimeTableUpdateRequestDTO request);
}
