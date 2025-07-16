package com.lunchchat.domain.member.converter;

import com.lunchchat.domain.member.dto.MemberRecommendationResponseDTO;
import com.lunchchat.domain.member.entity.Member;
import java.util.List;

public class MemberRecommendationConverter {

    public static MemberRecommendationResponseDTO toRecommendationResponse(
            Member member,
            int matchedTimeTableCount,
            int matchedInterestsCount) {

        return MemberRecommendationResponseDTO.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileIntro(member.getProfileIntro())
                .profileImageUrl(member.getProfileImageUrl())
                .matchedTimeTableCount(matchedTimeTableCount)
                .matchedInterestsCount(matchedInterestsCount)
                .build();
    }
}

