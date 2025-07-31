package com.lunchchat.domain.notification.controller;

import com.lunchchat.domain.notification.dto.response.NotificationCursorResponseDTO;
import com.lunchchat.domain.notification.service.NotificationQueryService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 API", description = "매칭 관련 알림 API")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "커서 기반 알림 목록 조회")
    public ApiResponse<NotificationCursorResponseDTO> getNotifications(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "마지막 알림 ID (첫 요청시 생략)")
        @RequestParam(required = false) Long lastNotificationId,
        @Parameter(description = "가져올 개수")
        @RequestParam(defaultValue = "20") int size) {

        NotificationCursorResponseDTO notifications = notificationQueryService.getNotificationsWithCursor(
            userDetails.getUsername(), lastNotificationId, size);

        return ApiResponse.onSuccess(notifications);
    }
}
