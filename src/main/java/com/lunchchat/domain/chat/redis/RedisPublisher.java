// src/main/java/com/lunchchat/domain/chat/redis/RedisPublisher.java

package com.lunchchat.domain.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 메시지를 특정 채팅방의 고유 채널로 발행합니다. 예: roomId가 123이면 "chat:room:123" 채널로 메시지를 전송합니다.
     *
     * @param roomId  채팅방 ID
     * @param message 전송할 메시지 객체
     */
    public void publishToRoom(Long roomId, Object message) {
        String channel = "chat:room:" + roomId;
        redisTemplate.convertAndSend(channel, message);
    }
}