package com.lunchchat.domain.member.service;

import java.util.List;

public interface MemberCommandService {

    void updateFcmToken(String email, String fcmToken);
    void updateInterests(String email, List<Long> interestIds);
}
