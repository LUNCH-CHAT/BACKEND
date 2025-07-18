package com.lunchchat.domain.match.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.notification.dto.FcmSendDto;
import com.lunchchat.domain.notification.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatchNotificationServiceImpl implements MatchNotificationService {

    private final FcmService fcmService;

    @Override
    public void sendMatchRequestNotification(Member fromMember, Member toMember) {
        String title = "새로운 매칭 요청";
        String body = fromMember.getMembername() + "님이 매칭을 요청했습니다.";
        Map<String, String> data = Map.of(
                "type", "MATCH_REQUEST",
                "senderId", String.valueOf(fromMember.getId()),
                "senderName", fromMember.getMembername()
        );

        FcmSendDto fcmSendDto = FcmSendDto.builder()
                .userId(toMember.getId())
                .title(title)
                .body(body)
                .data(data)
                .build();

        fcmService.sendNotification(fcmSendDto);
    }

    @Override
    public void sendMatchAcceptNotification(Member fromMember, Member toMember) {
        String title = "매칭 수락";
        String body = toMember.getMembername() + "님이 매칭을 수락했습니다.";
        Map<String, String> data = Map.of(
                "type", "MATCH_ACCEPT",
                "accepterId", String.valueOf(toMember.getId()),
                "accepterName", toMember.getMembername()
        );

        FcmSendDto fcmSendDto = FcmSendDto.builder()
                .userId(fromMember.getId())
                .title(title)
                .body(body)
                .data(data)
                .build();

        fcmService.sendNotification(fcmSendDto);
    }
}
