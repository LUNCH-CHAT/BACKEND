package com.lunchchat.domain.notification.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FcmSendDto {

    private final Long userId;
    private final String title;
    private final String body;
    private final Map<String, String> data;

    @Builder
    public FcmSendDto(Long userId, String title, String body, Map<String, String> data) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.data = data;
    }
}
