package com.lunchchat.domain.notification.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.notification.dto.FcmSendDto;
import com.lunchchat.domain.notification.entity.Notification;
import com.lunchchat.domain.notification.entity.enums.NotificationType;
import com.lunchchat.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandServiceImpl implements NotificationCommandService {
    
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;
    
    @Override
    public void createMatchRequestNotification(Member toMember, Member fromMember) {
        String content = String.format("%s님이 런치챗을 요청했어요!", fromMember.getMembername());
        
        // 1. DB에 알림 저장
        Notification notification = new Notification(toMember, fromMember, NotificationType.MATCH_REQUEST, content);
        notificationRepository.save(notification);
        
        // 2. FCM 푸시 알림 발송
        sendFcmNotification(toMember, "런치챗 요청", content, NotificationType.MATCH_REQUEST, fromMember);
        
        log.info("매칭 요청 알림 생성 완료 - 요청자: {}, 수신자: {}", fromMember.getId(), toMember.getId());
    }
    
    @Override
    public void createMatchAcceptedNotification(Member toMember, Member fromMember) {
        String content = String.format("%s님이 런치챗을 수락했어요!", fromMember.getMembername());
        
        // 1. DB에 알림 저장
        Notification notification = new Notification(toMember, fromMember, NotificationType.MATCH_ACCEPTED, content);
        notificationRepository.save(notification);
        
        // 2. FCM 푸시 알림 발송
        sendFcmNotification(toMember, "런치챗 수락", content, NotificationType.MATCH_ACCEPTED, fromMember);
        
        log.info("매칭 수락 알림 생성 완료 - 수락자: {}, 수신자: {}", fromMember.getId(), toMember.getId());
    }
    
    private void sendFcmNotification(Member toMember, String title, String body, String type, Member sender) {
        Map<String, String> data = new HashMap<>();
        data.put("type", type);
        data.put("senderId", sender.getId().toString());
        data.put("senderMembername", sender.getMembername()); // nickname → membername
        
        FcmSendDto fcmDto = FcmSendDto.builder()
                .userId(toMember.getId())
                .title(title)
                .body(body)
                .data(data)
                .build();
        
        fcmService.sendNotification(fcmDto);
    }
}
