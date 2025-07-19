package com.lunchchat.domain.member.converter;

import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.time_table.converter.TimeTableConverter;
import com.lunchchat.domain.user_interests.dto.UserInterestDTO;
import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberConverter {

    private final TimeTableConverter timeTableConverter;

    public MemberResponseDTO.MemberDetailResponseDTO toMemberDetailResponse(Member member) {
        return MemberResponseDTO.MemberDetailResponseDTO.builder()
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
                .timeTables(member.getTimeTables().stream()
                        .map(timeTableConverter::toTimeTableDTO)
                        .collect(Collectors.toList()))
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    public static MemberResponseDTO.MyPageResponseDTO toMyPageDto(Member member,
        int completed, int requested, int received, List<String> keywords, List<String> tags) {

        return MemberResponseDTO.MyPageResponseDTO.builder()
            .profileImageUrl(member.getProfileImageUrl())
            .name(member.getMembername())
            .studentId(member.getStudentNo())
            .department(member.getDepartment().getName())
            .completed(completed)
            .requested(requested)
            .received(received)
            .keywords(keywords)
            .tags(tags)
            .build();
    }
}

