package com.lunchchat.domain.notification.controller;

import com.lunchchat.domain.notification.dto.FcmSendDto;
import com.lunchchat.domain.notification.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final FcmService fcmService;

    @PostMapping("/send")
    public void sendNotification(@RequestBody FcmSendDto fcmSendDto) {
        fcmService.sendNotification(fcmSendDto);
    }
}
