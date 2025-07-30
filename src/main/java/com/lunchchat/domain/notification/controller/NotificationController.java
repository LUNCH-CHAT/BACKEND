package com.lunchchat.domain.notification.controller;

import com.lunchchat.domain.notification.dto.response.NotificationResponseDTO;
import com.lunchchat.domain.notification.service.NotificationQueryService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 API", description = "매칭 관련 알림 API")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "특정 사용자의 매칭 관련 알림 목록을 조회합니다.")
    public ApiResponse<List<NotificationResponseDTO>> getNotifications(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<NotificationResponseDTO> notifications = notificationQueryService.getNotifications(userDetails.getUsername());

        return ApiResponse.onSuccess(notifications);
    }
}
