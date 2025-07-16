package com.lunchchat.domain.member.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberRecommendationResponseDTO {
    private Long memberId;
    private String nickname;
    private String profileIntro;
    private String profileImageUrl;
    private int matchedTimeTableCount;
    private int matchedInterestsCount;
}
