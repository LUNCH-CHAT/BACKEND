package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.dto.MemberFilterRequestDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.global.apiPayLoad.PaginatedResponse;

import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;
import java.util.List;

public interface MemberQueryService {
    MemberResponseDTO.MemberDetailResponseDTO getMemberDetail(Long memberId, String viewerEmail);
    List<MemberResponseDTO.MemberRecommendationResponseDTO> getRecommendedMembers(Long currentMemberId);
    PaginatedResponse<MemberResponseDTO.MemberRecommendationResponseDTO> getFilteredRecommendations(String currentMemberEmail, MemberFilterRequestDTO req);
    List<MemberResponseDTO.MemberRecommendationResponseDTO> getPopularMembers(Long currentMemberId);
    MemberResponseDTO.MyPageResponseDTO getMyPage(String email);
    List<UserKeywordDTO> getUserKeywords(String email);
}
