package com.lunchchat.domain.notification.service;

import com.lunchchat.domain.notification.dto.FcmSendDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenCacheService {
    
    private final StringRedisTemplate redisTemplate;
    
    private static final String CACHE_KEY_PREFIX = "fcm:token:";
    private static final Duration CACHE_TTL = Duration.ofHours(24);
    
    public String getFcmToken(Long userId) {
        String cacheKey = CACHE_KEY_PREFIX + userId;
        return redisTemplate.opsForValue().get(cacheKey);
    }
    
    public void updateFcmTokenCache(Long userId, String newToken) {
        String cacheKey = CACHE_KEY_PREFIX + userId;
        
        if (newToken != null) {
            redisTemplate.opsForValue().set(cacheKey, newToken, CACHE_TTL);
        } else {
            redisTemplate.delete(cacheKey);
        }
    }
    
    public void evictFcmTokenCache(Long userId) {
        String cacheKey = CACHE_KEY_PREFIX + userId;
        redisTemplate.delete(cacheKey);
    }
}
