package com.lunchchat.domain.notification.service;

import com.lunchchat.domain.notification.dto.FcmSendDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {

    public void sendNotification(FcmSendDto fcmSendDto) {
        // 실제 FCM 전송 로직 구현
        System.out.println("Sending FCM notification to: " + fcmSendDto.getToken());
    }
}
