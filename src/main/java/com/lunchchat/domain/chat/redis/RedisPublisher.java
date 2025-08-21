package com.lunchchat.domain.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    // 메시지를 발행
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

}
