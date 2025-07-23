package com.lunchchat.domain.time_table.dto;

import java.time.LocalTime;

public record TimeTableDTO (
    String dayOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    String subjectName
){}
