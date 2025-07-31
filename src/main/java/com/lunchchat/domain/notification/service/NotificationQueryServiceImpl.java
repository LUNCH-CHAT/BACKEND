package com.lunchchat.domain.notification.service;

import com.lunchchat.domain.notification.dto.response.NotificationCursorResponseDTO;
import com.lunchchat.domain.notification.dto.response.NotificationResponseDTO;
import com.lunchchat.domain.notification.entity.Notification;
import com.lunchchat.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {
    
    private final NotificationRepository notificationRepository;
    
    @Override
    public NotificationCursorResponseDTO getNotificationsWithCursor(String email, Long lastNotificationId, int size) {
        Pageable pageable = PageRequest.of(0, size + 1);
        
        List<Notification> notifications;
        
        if (lastNotificationId == null) {
            notifications = notificationRepository.findFirstNotificationsByEmail(email, pageable);
        } else {
            notifications = notificationRepository.findNextNotificationsByEmailAndLastId(email, lastNotificationId, pageable);
        }
        
        boolean hasNext = notifications.size() > size;
        if (hasNext) {
            notifications = notifications.subList(0, size);
        }
        
        List<NotificationResponseDTO> responseList = notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
        
        return NotificationCursorResponseDTO.of(responseList, hasNext);
    }
}
