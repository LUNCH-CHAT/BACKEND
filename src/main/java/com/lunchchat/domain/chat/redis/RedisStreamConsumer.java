//package com.lunchchat.domain.chat.redis;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
//import jakarta.annotation.PostConstruct;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.connection.stream.Consumer;
//import org.springframework.data.redis.connection.stream.MapRecord;
//import org.springframework.data.redis.connection.stream.ReadOffset;
//import org.springframework.data.redis.connection.stream.StreamOffset;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.stream.StreamListener;
//import org.springframework.data.redis.stream.StreamMessageListenerContainer;
//import org.springframework.data.redis.stream.Subscription;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class RedisStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final SimpMessageSendingOperations messagingTemplate;
//    private final ObjectMapper objectMapper;
//
//    private static final String STREAM_KEY = "chat-stream";
//    private static final String GROUP = "chat-group";   // 모든 서버가 같은 그룹
//    private static final String CONSUMER = "chat-consumer-" + System.currentTimeMillis();
//
//    @PostConstruct
//    public void init() {
//        try {
//            // 처음 실행 시 Stream에 Consumer Group 생성
//            redisTemplate.opsForStream().createGroup(STREAM_KEY, GROUP);
//        } catch (Exception e) {
//            log.info("Consumer group already exists: {}", GROUP);
//        }
//
//        // ListenerContainer 설정
//        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
//                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
//                        .builder()
//                        .pollTimeout(Duration.ofSeconds(2))
//                        .build();
//
//        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
//                StreamMessageListenerContainer.create(redisTemplate.getConnectionFactory(), options); //ListenerContainer 시작
//        //내부적으로 group에 속한 consumer는 XREADGROUP 명령을 실행해 메시지를 Polling
//
//
//        Subscription subscription = container.receive(
//                Consumer.from(GROUP, CONSUMER),
//                StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()),
//                this
//        );
//
//        container.start();
//        log.info("Redis Stream Consumer started with consumer: {}", CONSUMER);
//    }
//
//    // producer가 발행한 메시지를 chat-group 내 한 Consumer에게 전송
//    @Override
//    public void onMessage(MapRecord<String, String, String> message) {
//        try {
//            Map<String, String> map = message.getValue();
//
//            ChatMessageRes chatMessage = new ChatMessageRes(
//                    map.get("id"),
//                    Long.parseLong(map.get("roomId").replace("\"", "")),
//                    Long.parseLong(map.get("senderId").replace("\"", "")),
//                    map.get("content"),
//                    LocalDateTime.parse(map.get("createdAt").replace("\"", "")) // ISO-8601 문자열로 변환
//            );
//
//            messagingTemplate.convertAndSend("/sub/rooms/" + chatMessage.roomId(), chatMessage);
//            log.info("Consumed message: {}", chatMessage);
//
//            // ack 처리 (동일 메시지를 여러 consumer가 처리하지 않도록)
//            redisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP, message.getId());
//
//        } catch (Exception e) {
//            log.error("Redis Stream 메시지 처리 실패", e);
//        }
//    }
//}
