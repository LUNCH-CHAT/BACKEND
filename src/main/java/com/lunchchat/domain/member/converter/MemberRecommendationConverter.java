package com.lunchchat.domain.member.converter;

import com.lunchchat.domain.member.dto.MemberRecommendationResponseDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.user_interests.dto.UserInterestDTO;
import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MemberRecommendationConverter {

    public static MemberRecommendationResponseDTO toRecommendationResponse(
            Member member) {

        List<UserInterestDTO> interests = member.getUserInterests() != null
                ? member.getUserInterests().stream()
                .filter(ui -> ui.getInterests() != null)
                .map(UserInterestDTO::from)
                .collect(Collectors.toList())
                : Collections.emptyList();

        List<UserKeywordDTO> keywords = member.getUserKeywords() != null
                ? member.getUserKeywords().stream()
                .map(UserKeywordDTO::from)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return MemberRecommendationResponseDTO.builder()
                .memberId(member.getId())
                .memberName(member.getMembername())
                .profileImageUrl(member.getProfileImageUrl())
                .studentNo(member.getStudentNo())
                .department(member.getDepartment() != null ? member.getDepartment().getName() : null)
                .userInterests(interests)
                .userKeywords(keywords)
                .build();
    }
}
