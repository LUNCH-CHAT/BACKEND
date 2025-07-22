package com.lunchchat.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.notification.dto.FcmSendDto;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;

    // FirebaseMessaging을 선택적으로 주입받도록 수정 (required = false)
    public FcmService(@Autowired(required = false) FirebaseMessaging firebaseMessaging, 
                     MemberRepository memberRepository) {
        this.firebaseMessaging = firebaseMessaging;
        this.memberRepository = memberRepository;
        
        if (firebaseMessaging == null) {
            log.warn("⚠️  FirebaseMessaging이 사용할 수 없습니다. FCM 알림 기능이 비활성화됩니다.");
        } else {
            log.info("✅ FirebaseMessaging이 정상적으로 초기화되었습니다. FCM 알림 기능이 활성화됩니다.");
        }
    }

    public void sendNotification(FcmSendDto dto) {
        // FirebaseMessaging이 없으면 조용히 무시
        if (firebaseMessaging == null) {
            log.warn("🚫 FCM이 비활성화되어 있어 알림을 보낼 수 없습니다. userId: {}, title: {}", 
                    dto.getUserId(), dto.getTitle());
            return;
        }

        Member member = memberRepository.findById(dto.getUserId())
            .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));

        if (member.getFcmToken() == null || member.getFcmToken().isEmpty()) {
            log.warn("FCM 토큰이 비어있어 알림을 보낼 수 없습니다. userId: {}", dto.getUserId());
            return;
        }

        Notification notification = Notification.builder()
            .setTitle(dto.getTitle())
            .setBody(dto.getBody())
            .build();

        Message message = Message.builder()
            .setToken(member.getFcmToken())
            .setNotification(notification)
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
