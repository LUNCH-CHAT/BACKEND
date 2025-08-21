package com.lunchchat.domain.chat.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import java.nio.charset.StandardCharsets;
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

    // Redis에서 메시지를 수신 시 호출되는 메서드
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msgBody = new String(message.getBody(), StandardCharsets.UTF_8);
            ChatMessageRes chatMessage = objectMapper.readValue(msgBody, ChatMessageRes.class);

            messagingTemplate.convertAndSend("/sub/rooms/" + chatMessage.roomId(), chatMessage);

        } catch (Exception e) {
            log.error("Redis 구독 메시지 처리 실패", e);
        }
    }

}
