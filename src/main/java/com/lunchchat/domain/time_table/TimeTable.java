package com.lunchchat.domain.time_table;

import com.lunchchat.domain.users.User;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeTable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeTableType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private String subjectName;

    public enum TimeTableType {
        TIMETABLE,
        COFFEECHAT
    }

    public enum DayOfWeek {
        MON, TUE, WED, THU, FRI, SAT, SUN
    }
}
