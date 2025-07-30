package com.lunchchat.domain.member.service;

import java.util.List;

public interface MemberCommandService {

    void updateFcmToken(Long memberId, String fcmToken);
    void updateInterests(String email, List<Long> interestIds);
}
