package com.lunchchat.domain.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    
    // 처리된 메시지 추적 (메모리 기반 중복 제거)
    private final Set<String> processedMessages = ConcurrentHashMap.newKeySet();

    // Redis에서 메시지를 수신 시 호출되는 메서드
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msgBody = new String(message.getBody(), StandardCharsets.UTF_8);
            ChatMessageRes chatMessage = objectMapper.readValue(msgBody, ChatMessageRes.class);
            
            // 메시지 중복 체크 (메시지 ID만 사용 - 더 확실함)
            String messageKey = String.valueOf(chatMessage.id());
            
            if (!processedMessages.add(messageKey)) {
                log.debug("중복 메시지 무시: {}", messageKey);
                return;
            }
            
            // 메모리 누수 방지 (5000개 초과시 절반 제거)
            if (processedMessages.size() > 5000) {
                processedMessages.clear();
                log.info("처리된 메시지 캐시 초기화");
            }
            
            log.debug("메시지 전송: roomId={}, messageId={}", chatMessage.roomId(), chatMessage.id());
            messagingTemplate.convertAndSend("/sub/rooms/" + chatMessage.roomId(), chatMessage);

        } catch (Exception e) {
            log.error("Redis 구독 메시지 처리 실패", e);
        }
    }

}
