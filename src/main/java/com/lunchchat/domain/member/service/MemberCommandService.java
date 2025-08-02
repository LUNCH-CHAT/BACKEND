package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.dto.MemberRequestDTO;
import java.util.List;

public interface MemberCommandService {

    void updateFcmToken(String email, String fcmToken);
    void updateInterests(String email, List<Long> interestIds);
    void updateKeywords(String email, MemberRequestDTO.UpdateKeywordListDTO keywords);
}
