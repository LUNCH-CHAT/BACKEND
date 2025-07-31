package com.lunchchat.domain.notification.service;

import com.lunchchat.domain.notification.dto.response.NotificationCursorResponseDTO;

public interface NotificationQueryService {
    
    NotificationCursorResponseDTO getNotificationsWithCursor(String email, Long lastNotificationId, int size);
}
