package com.lunchchat.domain.time_table.dto;

import java.util.List;

public record TimeTableUpdateRequestDTO(
    List<TimeTableDTO> timeTableList
) {}