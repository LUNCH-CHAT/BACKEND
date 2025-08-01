package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.dto.MemberFilterRequestDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO;

import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;
import java.util.List;

public interface MemberQueryService {
    MemberResponseDTO.MemberDetailResponseDTO getMemberDetail(Long memberId, String viewerEmail);
    List<MemberResponseDTO.MemberRecommendationResponseDTO> getRecommendedMembers(Long currentMemberId);
    MemberResponseDTO.MyPageResponseDTO getMyPage(String email);
    List<MemberResponseDTO.MemberRecommendationResponseDTO> getFilteredRecommendations(String email, MemberFilterRequestDTO req);
    List<UserKeywordDTO> getUserKeywords(String email);
}
