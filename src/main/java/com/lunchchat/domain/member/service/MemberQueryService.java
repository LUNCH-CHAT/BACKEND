package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.dto.MemberDetailResponseDTO;

public interface MemberQueryService {
    MemberDetailResponseDTO getMemberDetail(Long memberId);
}
