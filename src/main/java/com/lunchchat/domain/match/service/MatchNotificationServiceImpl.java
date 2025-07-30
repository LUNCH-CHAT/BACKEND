package com.lunchchat.domain.match.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.notification.service.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchNotificationServiceImpl implements MatchNotificationService {

    private final NotificationCommandService notificationCommandService;

    @Override
    public void sendMatchRequestNotification(Member fromMember, Member toMember) {
        notificationCommandService.createMatchRequestNotification(toMember, fromMember);

        log.info("매칭 요청 알림 완료 - 요청자: {} → 수신자: {}",
                fromMember.getId(), toMember.getId());
    }

    @Override
    public void sendMatchAcceptNotification(Member fromMember, Member toMember) {
        notificationCommandService.createMatchAcceptedNotification(fromMember, toMember);

        log.info("매칭 수락 알림 완료 - 수락자: {} → 요청자: {}",
                toMember.getId(), fromMember.getId());
    }
}
