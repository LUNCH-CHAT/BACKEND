package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.dto.MemberResponseDTO;

import java.util.List;

public interface MemberQueryService {
    MemberResponseDTO.MemberDetailResponseDTO getMemberDetail(Long memberId);
    List<MemberResponseDTO.MemberRecommendationResponseDTO> getRecommendedMembers(Long currentMemberId);
    MemberResponseDTO.MyPageResponseDTO getMyPage(Long memberId);
}
