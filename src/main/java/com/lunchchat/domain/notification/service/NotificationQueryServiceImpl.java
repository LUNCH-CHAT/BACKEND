package com.lunchchat.domain.notification.service;

import com.lunchchat.domain.notification.dto.response.NotificationResponseDTO;
import com.lunchchat.domain.notification.entity.Notification;
import com.lunchchat.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<NotificationResponseDTO> getNotifications(Long memberId) {
        List<Notification> notifications = notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        
        return notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
    }
}
