package com.lunchchat.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunchchat.domain.notification.dto.FcmSendDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationQueueService {
    
    private final StringRedisTemplate redisTemplate;
    private final FcmService fcmService;
    private final ObjectMapper objectMapper;
    
    private static final String QUEUE_KEY = "notification-queue";
    
    public void enqueue(FcmSendDto dto) {
        try {
            String jsonDto = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForList().leftPush(QUEUE_KEY, jsonDto);
        } catch (Exception e) {
            log.error("알림 큐 추가 실패 - userId: {}", dto.getUserId(), e);
        }
    }
    
    @Scheduled(fixedDelay = 500)
    public void processQueue() {
        try {
            String jsonDto = redisTemplate.opsForList().rightPop(QUEUE_KEY);
            
            if (jsonDto != null) {
                FcmSendDto dto = objectMapper.readValue(jsonDto, FcmSendDto.class);
                fcmService.sendNotification(dto);
            }
        } catch (Exception e) {
            log.error("큐 알림 처리 실패", e);
        }
    }
    
    public long getQueueSize() {
        Long size = redisTemplate.opsForList().size(QUEUE_KEY);
        return size != null ? size : 0;
    }
}
