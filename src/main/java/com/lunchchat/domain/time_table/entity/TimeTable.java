package com.lunchchat.domain.time_table.entity;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private String subjectName;

    //setter

    public void setMember(Member member) {
        this.member = member;
    }

    //builder
    public static TimeTable create(DayOfWeek day, LocalTime start, LocalTime end, String subject) {
        TimeTable tt = new TimeTable();
        tt.dayOfWeek = day;
        tt.startTime = start;
        tt.endTime = end;
        tt.subjectName = subject;
        return tt;
    }

}
