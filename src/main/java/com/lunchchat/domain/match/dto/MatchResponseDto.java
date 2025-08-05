package com.lunchchat.domain.match.dto;

import com.lunchchat.domain.match.entity.MatchStatus;
import com.lunchchat.domain.member.entity.enums.InterestType;

import java.time.LocalDateTime;
import java.util.List;

public class MatchResponseDto {

    public record MatchListDto(
        Long id,
        LocalDateTime createdAt,
        MatchedUserDto matchedUser
    ) {}

    public record MatchedUserDto(
        Long id,
        String memberName,
        String studentNo,
        String department,
        String profileImageUrl,
        List<KeywordDto> userKeywords,
        List<InterestDto> userInterests
    ) {}

    public record KeywordDto(
        Long id,
        String keywordName
    ) {}

    public record InterestDto(
        Long id,
        InterestType interestName
    ) {}

    public record MatchResultDto(
        Long id,
        MatchStatus status,
        LocalDateTime createdAt
    ) {}
}