package com.lunchchat.domain.match.service;

import com.lunchchat.domain.member.entity.Member;

public interface MatchNotificationService {

    void sendMatchRequestNotification(Member fromMember, Member toMember);

    void sendMatchAcceptNotification(Member fromMember, Member toMember);
}
