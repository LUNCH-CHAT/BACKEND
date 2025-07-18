package com.lunchchat.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmUpdateRequestDto {
    @Schema(description = "FCM 토큰", example = "c8K_8_U-p_4:APA91bH...")
    private String fcmToken;
}
