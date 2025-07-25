package com.lunchchat.domain.time_table.dto;

import com.lunchchat.domain.time_table.entity.DayOfWeek;
import java.time.LocalTime;

public record TimeTableDTO (
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    String subjectName
){}
