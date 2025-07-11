package com.lunchchat.domain.member.converter;

import com.lunchchat.domain.member.dto.MemberDetailResponseDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.user_interests.dto.UserInterestDTO;
import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MemberConverter {

    public MemberDetailResponseDTO toMemberDetailResponse(Member member) {
        return MemberDetailResponseDTO.builder()
                .id(member.getId())
                .memberName(member.getMembername())
                .email(member.getEmail())
                .studentNo(member.getStudentNo())
                .profileImageUrl(member.getProfileImageUrl())
                .university(member.getUniversity().getName())
                .college(member.getCollege().getName())
                .department(member.getDepartment().getName())
                .userKeywords(member.getUserKeywords().stream()
                        .map(UserKeywordDTO::from)
                        .collect(Collectors.toList()))
                .userInterests(member.getUserInterests().stream()
                        .map(UserInterestDTO::from)
                        .collect(Collectors.toList()))
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}

