package com.lunchchat.domain.time_table.converter;

import com.lunchchat.domain.time_table.dto.TimeTableDTO;
import com.lunchchat.domain.time_table.entity.TimeTable;
import org.springframework.stereotype.Component;

@Component
public class TimeTableConverter {

    public TimeTableDTO toTimeTableDTO(TimeTable timeTable) {
        return TimeTableDTO.builder()
                .dayOfWeek(timeTable.getDayOfWeek().name())
                .startTime(timeTable.getStartTime())
                .endTime(timeTable.getEndTime())
                .subjectName(timeTable.getSubjectName())
                .build();
    }
}
