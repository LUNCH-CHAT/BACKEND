package com.lunchchat.domain.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

// @Component  // Redis Streams 사용으로 비활성화
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    // 메시지를 발행
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

}
