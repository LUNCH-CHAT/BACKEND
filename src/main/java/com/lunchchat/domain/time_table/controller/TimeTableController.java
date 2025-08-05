package com.lunchchat.domain.time_table.controller;

import com.lunchchat.domain.time_table.dto.TimeTableUpdateRequestDTO;
import com.lunchchat.domain.time_table.service.TimeTableCommandService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.code.status.SuccessStatus;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timetable")
public class TimeTableController {
  private final TimeTableCommandService timeTableCommandService;

  @PutMapping
  @Operation(summary = "시간표 업데이트", description = "사용자의 시간표를 업데이트합니다.")
  public ApiResponse<SuccessStatus> updateTimeTable(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody TimeTableUpdateRequestDTO request
  ) {
    timeTableCommandService.updateTimeTable(userDetails.getUsername(), request);
    return ApiResponse.onSuccess(SuccessStatus.TIME_TABLE_UPDATE_SUCCESS);
  }

}
