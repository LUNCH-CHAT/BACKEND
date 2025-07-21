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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final MemberRepository memberRepository;

    public void sendNotification(FcmSendDto dto) {
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
            log.info("FCM 알림 전송 성공. userId: {}", dto.getUserId());
        } catch (FirebaseMessagingException e) {
            log.error("FCM 알림 전송 실패. userId: {}", dto.getUserId(), e);
        }
    }
}
