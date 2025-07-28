package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.dto.MemberFilterRequestDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO;

import java.util.List;

public interface MemberQueryService {
    MemberResponseDTO.MemberDetailResponseDTO getMemberDetail(Long memberId, Long viewerId);
    List<MemberResponseDTO.MemberRecommendationResponseDTO> getRecommendedMembers(Long currentMemberId);
    MemberResponseDTO.MyPageResponseDTO getMyPage(Long memberId);
    List<MemberResponseDTO.MemberRecommendationResponseDTO> getFilteredRecommendations(Long currentMemberId, MemberFilterRequestDTO req);
}
