package com.lunchchat.domain.member.dto;

import com.lunchchat.domain.user_interests.dto.UserInterestDTO;
import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MemberDetailResponseDTO {

    private Long id;
    private String memberName;
    private String nickname;
    private String email;
    private String studentNo;
    private String university;
    private String college;
    private String department;
    private String profileIntro;
    private String profileImageUrl;

    private List<UserKeywordDTO> userKeywords;
    private List<UserInterestDTO> userInterests;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


