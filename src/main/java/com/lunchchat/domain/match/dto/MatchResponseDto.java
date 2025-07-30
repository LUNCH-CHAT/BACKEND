package com.lunchchat.domain.match.dto;

import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.member.entity.enums.InterestType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class MatchResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchListDto {
        private Long id;
        private LocalDateTime createdAt;
        private MatchedUserDto matchedUser;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchedUserDto {
        private Long id;
        private String memberName;
        private String studentNo;
        private String department;
        private String profileImageUrl;
        private List<KeywordDto> userKeywords;
        private List<InterestDto> userInterests;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordDto {
        private Long id;
        private String keywordName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterestDto {
        private Long id;
        private InterestType interestName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchResultDto {
        private Long id;
        private MatchStatus status;
        private LocalDateTime createdAt;
    }
}
