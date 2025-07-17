package com.lunchchat.domain.time_table.dto;

import com.lunchchat.domain.time_table.entity.TimeTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class TimeTableDTO {
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subjectName;

}
