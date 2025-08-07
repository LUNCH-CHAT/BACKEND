package com.lunchchat.domain.member.converter;

import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.entity.enums.InterestType;
import com.lunchchat.domain.time_table.converter.TimeTableConverter;
import com.lunchchat.domain.user_interests.dto.UserInterestDTO;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemberConverter {

    private final TimeTableConverter timeTableConverter;

    public MemberResponseDTO.MemberDetailResponseDTO toMemberDetailResponse(Member member, MatchStatusType matchStatus) {
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
                .userInterests(member.getInterests().stream()
                        .map(Interest::getType)
                        .collect(Collectors.toList()))
                .timeTables(member.getTimeTables().stream()
                        .map(timeTableConverter::toTimeTableDTO)
                        .collect(Collectors.toList()))
                .matchStatus(matchStatus)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    public static MemberResponseDTO.MyPageResponseDTO toMyPageDto(Member member,
        int completed, int requested, int received, List<String> keywords, List<InterestType> tags) {

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

    public MemberResponseDTO.MyProfileDetailResponseDTO toMyProfileDetailResponse(Member member) {
        return MemberResponseDTO.MyProfileDetailResponseDTO.builder()
            .profileImageUrl(member.getProfileImageUrl())
            .memberName(member.getMembername())
            .studentNo(member.getStudentNo())
            .department(member.getDepartment().getName())
            .userInterests(member.getInterests().stream()
                .map(Interest::getType)
                .collect(Collectors.toList()))
            .userKeywords(member.getUserKeywords().stream()
                .map(UserKeywordDTO::from)
                .collect(Collectors.toList()))
            .timeTables(member.getTimeTables().stream()
                .map(timeTableConverter::toTimeTableDTO)
                .collect(Collectors.toList()))
            .build();
    }

    public static MemberResponseDTO.PresignedUrlResponse toPresignedUrlResponse(String presignedUrl, String s3Url) {
        return MemberResponseDTO.PresignedUrlResponse.builder()
            .presignedUrl(presignedUrl)
            .s3Url(s3Url)
            .build();
    }
}

