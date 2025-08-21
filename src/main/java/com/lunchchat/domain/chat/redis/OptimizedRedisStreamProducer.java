package com.lunchchat.domain.chat.redis;

import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OptimizedRedisStreamProducer {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String STREAM_PREFIX = "chat-stream:";

    public void publishToRoom(Long roomId, ChatMessageRes message) {
        try {
            String streamKey = STREAM_PREFIX + roomId;
            
            // ObjectRecord 사용으로 직렬화 최적화
            ObjectRecord<String, ChatMessageRes> record = ObjectRecord.create(streamKey, message);
            
            RecordId recordId = redisTemplate.opsForStream().add(record);
            
            log.debug("Message published to stream {}: recordId={}", streamKey, recordId);
        } catch (Exception e) {
            log.error("Redis Stream publish 실패 - roomId: {}", roomId, e);
        }
    }
}