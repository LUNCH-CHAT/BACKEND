package com.lunchchat.domain.member.dto;

import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.member.entity.enums.InterestType;
import com.lunchchat.domain.time_table.dto.TimeTableDTO;
import com.lunchchat.domain.user_interests.dto.UserInterestDTO;
import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberRecommendationResponseDTO {
        private Long memberId;
        private String memberName;
        private String profileImageUrl;
        private String studentNo;
        private String department;
        private List<InterestType> userInterests;
        private List<UserKeywordDTO> userKeywords;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemberDetailResponseDTO {
        private Long id;
        private String memberName;
        private String email;
        private String studentNo;
        private String university;
        private String college;
        private String department;
        private String profileImageUrl;

        private List<UserKeywordDTO> userKeywords;
        private List<InterestType> userInterests;
        private List<TimeTableDTO> timeTables;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @Schema(description = "상대 사용자와의 매칭 상태 (ACCEPTED, REQUESTED, RECEIVED, NONE)")
        private MatchStatusType matchStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageResponseDTO {
        private String profileImageUrl;
        private String name;
        private String studentId;
        private String department;
        private int completed;
        private int requested;
        private int received;
        private List<String> keywords;
        private List<InterestType> tags;

    }
}

