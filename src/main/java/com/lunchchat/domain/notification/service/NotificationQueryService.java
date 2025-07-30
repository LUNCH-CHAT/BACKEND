package com.lunchchat.domain.notification.service;

import com.lunchchat.domain.notification.dto.response.NotificationResponseDTO;

import java.util.List;

public interface NotificationQueryService {
    
    List<NotificationResponseDTO> getNotifications(String email);
}
