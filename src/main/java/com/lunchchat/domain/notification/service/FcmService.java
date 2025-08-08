package com.lunchchat.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.lunchchat.domain.notification.dto.FcmSendDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenCacheService fcmTokenCacheService;

    public FcmService(@Autowired(required = false) FirebaseMessaging firebaseMessaging, 
                     FcmTokenCacheService fcmTokenCacheService) {
        this.firebaseMessaging = firebaseMessaging;
        this.fcmTokenCacheService = fcmTokenCacheService;
        
        if (firebaseMessaging == null) {
            log.warn("⚠️  FirebaseMessaging이 사용할 수 없습니다. FCM 알림 기능이 비활성화됩니다.");
        } else {
            log.info("✅ FirebaseMessaging이 정상적으로 초기화되었습니다. FCM 알림 기능이 활성화됩니다.");
        }
    }

    public void sendNotification(FcmSendDto dto) {
        if (firebaseMessaging == null) {
            log.warn("🚫 FCM이 비활성화되어 있어 알림을 보낼 수 없습니다. userId: {}, title: {}", 
                    dto.getUserId(), dto.getTitle());
            return;
        }

        // 캐시에서 FCM 토큰 조회
        String fcmToken = fcmTokenCacheService.getFcmToken(dto.getUserId());
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("FCM 토큰이 비어있어 알림을 보낼 수 없습니다. userId: {}", dto.getUserId());
            return;
        }

        Message message = Message.builder()
            .setToken(fcmToken)
            .putData("title", dto.getTitle())
            .putData("body", dto.getBody())
            .putAllData(dto.getData())
            .build();

        try {
            firebaseMessaging.send(message);
            log.info("✅ FCM 알림 전송 성공. userId: {}", dto.getUserId());
        } catch (FirebaseMessagingException e) {
            log.error("❌ FCM 알림 전송 실패. userId: {}", dto.getUserId(), e);
        }
    }
}
