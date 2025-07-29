package com.lunchchat.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDuplicationService {
    
    private final StringRedisTemplate redisTemplate;
    
    private static final String DUPLICATE_KEY_PREFIX = "notif:";
    private static final Duration DUPLICATE_PREVENTION_TTL = Duration.ofMinutes(5);
    
    public boolean isDuplicate(Long userId, String type, Long relatedId) {
        String key = String.format("%s%d:%s:%d", DUPLICATE_KEY_PREFIX, userId, type, relatedId);
        
        Boolean isNew = redisTemplate.opsForValue()
            .setIfAbsent(key, "sent", DUPLICATE_PREVENTION_TTL);
            
        boolean isDuplicate = !Boolean.TRUE.equals(isNew);
        
        if (isDuplicate) {
            log.warn("중복 알림 감지 - userId: {}, type: {}", userId, type);
        }
        
        return isDuplicate;
    }
    
    public void clearDuplicationKey(Long userId, String type, Long relatedId) {
        String key = String.format("%s%d:%s:%d", DUPLICATE_KEY_PREFIX, userId, type, relatedId);
        redisTemplate.delete(key);
    }
}
