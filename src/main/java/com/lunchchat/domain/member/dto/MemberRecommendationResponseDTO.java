package com.lunchchat.domain.member.dto;

import com.lunchchat.domain.user_interests.dto.UserInterestDTO;
import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberRecommendationResponseDTO {
    private Long memberId;
    private String memberName;
    private String profileImageUrl;
    private String studentNo;
    private String department;
    private List<UserInterestDTO> userInterests;
    private List<UserKeywordDTO> userKeywords;
}

