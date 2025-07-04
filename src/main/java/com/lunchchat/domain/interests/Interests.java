package com.lunchchat.domain.interests;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Interests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private InterestName name;

    public enum InterestName {
        EXCHANGE_STUDENT, // 교환학생
        EMPLOYMENT_CAREER, // 취업/진로
        EXAM_PREPARATION, // 고시준비
        STARTUP, // 창업
        FOREIGN_LANGUAGE_STUDY, // 외국어 공부
        HOBBY_LEISURE, // 취미/여가
        SCHOOL_LIFE, // 학교생활
        ETC // 기타
    }
}
