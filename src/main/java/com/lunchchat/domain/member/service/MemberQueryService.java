package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.dto.MemberDetailResponseDTO;
import com.lunchchat.domain.member.dto.MemberRecommendationResponseDTO;

import java.util.List;

public interface MemberQueryService {
    MemberDetailResponseDTO getMemberDetail(Long memberId);

    List<MemberRecommendationResponseDTO> getRecommendedMembers(Long currentMemberId);
}
