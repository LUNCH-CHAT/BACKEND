package com.lunchchat.domain.notification.controller;

import com.lunchchat.domain.notification.dto.response.NotificationResponseDTO;
import com.lunchchat.domain.notification.service.NotificationQueryService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 API", description = "매칭 관련 알림 API")
public class NotificationController {
    
    private final NotificationQueryService notificationQueryService;
    
    @GetMapping("/{memberId}")
    @Operation(summary = "알림 목록 조회", description = "특정 사용자의 매칭 관련 알림 목록을 조회합니다.")
    public ApiResponse<List<NotificationResponseDTO>> getNotifications(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long memberId) {
        
        List<NotificationResponseDTO> notifications = notificationQueryService.getNotifications(memberId);
        
        return ApiResponse.onSuccess(notifications);
    }
}
