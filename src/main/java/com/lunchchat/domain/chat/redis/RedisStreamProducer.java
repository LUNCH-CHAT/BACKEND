package com.lunchchat.domain.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStreamProducer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String STREAM_KEY = "chat-stream"; // stream 이름

    public void produce(ChatMessageRes message) {
        try {
            // 모든 값을 문자열로 변환하여 Map에 저장
            Map<String, String> map = Map.of(
                    "id", message.id(),
                    "roomId", String.valueOf(message.roomId()),
                    "senderId", String.valueOf(message.senderId()),
                    "content", message.content(),
                    "createdAt", message.createdAt().toString() // ISO-8601 문자열
            );

            // MapRecord로 Stream에 추가
            redisTemplate.opsForStream().add(MapRecord.create(STREAM_KEY, map));

            log.info("Produced to stream {}: {}", STREAM_KEY, map);
        } catch (Exception e) {
            log.error("Redis Stream produce 실패", e);
        }
    }

//    public void produce(Object message) {
//        try {
//            //Redis는 chat-stream이라는 Stream에 새로운 메시지를 기록
//            ObjectRecord<String, Object> record = ObjectRecord.create(STREAM_KEY, message);
//            // 메시지마다 고유 RecordId가 부여
//            RecordId recordId = redisTemplate.opsForStream().add(record);
//            log.info("Produced to stream {} with id {}", STREAM_KEY, recordId);
//        } catch (Exception e) {
//            log.error("Redis Stream produce 실패", e);
//        }
//    }
}
