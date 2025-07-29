package com.lunchchat.domain.notification.service;

import com.lunchchat.domain.member.entity.Member;

public interface NotificationCommandService {

    void createMatchRequestNotification(Member toMember, Member fromMember);
    void createMatchAcceptedNotification(Member toMember, Member fromMember);
}
