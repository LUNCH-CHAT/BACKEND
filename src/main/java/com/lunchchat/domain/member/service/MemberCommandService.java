package com.lunchchat.domain.member.service;

public interface MemberCommandService {

    void updateFcmToken(String email, String fcmToken);
}
