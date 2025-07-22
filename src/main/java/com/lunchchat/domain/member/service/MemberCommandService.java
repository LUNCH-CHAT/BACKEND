package com.lunchchat.domain.member.service;

public interface MemberCommandService {

    void updateFcmToken(Long memberId, String fcmToken);
}
