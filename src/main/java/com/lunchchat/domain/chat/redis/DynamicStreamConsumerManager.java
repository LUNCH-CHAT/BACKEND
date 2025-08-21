package com.lunchchat.domain.chat.redis;

import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicStreamConsumerManager implements StreamListener<String, ObjectRecord<String, ChatMessageRes>> {

    private final RedisTemplate<String, Object> redisTemplate;
    @Lazy
    private final SimpMessageSendingOperations messagingTemplate;
    
    private static final String STREAM_PREFIX = "chat-stream:";
    private static final String CONSUMER_GROUP = "chat-consumer-group";
    private final String consumerId = "consumer-" + System.currentTimeMillis();
    
    @SuppressWarnings("rawtypes")
    private StreamMessageListenerContainer listenerContainer;
    private final Map<Long, Subscription> activeSubscriptions = new ConcurrentHashMap<>();

    @PostConstruct
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void initContainer() {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofMillis(100))  // 100ms로 단축
                        .build();

        listenerContainer = StreamMessageListenerContainer.create(
                redisTemplate.getConnectionFactory(), options);
        listenerContainer.start();
        
        log.info("🚀 Dynamic Stream Consumer Manager initialized with consumerId: {}", consumerId);
    }

    public void subscribeToRoom(Long roomId) {
        if (activeSubscriptions.containsKey(roomId)) {
            log.debug("Already subscribed to room: {}", roomId);
            return;
        }

        try {
            String streamKey = STREAM_PREFIX + roomId;
            
            // Consumer Group 생성 (이미 존재하면 무시)
            try {
                redisTemplate.opsForStream().createGroup(streamKey, CONSUMER_GROUP);
            } catch (Exception e) {
                log.debug("Consumer group already exists for stream: {}", streamKey);
            }

            // 채팅방별 구독 시작
            @SuppressWarnings("unchecked")
            Subscription subscription = listenerContainer.receive(
                    Consumer.from(CONSUMER_GROUP, consumerId),
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                    this
            );

            activeSubscriptions.put(roomId, subscription);
            log.info("🎯 Subscribed to room stream: {}", streamKey);
            
        } catch (Exception e) {
            log.error("Failed to subscribe to room: {}", roomId, e);
        }
    }

    public void unsubscribeFromRoom(Long roomId) {
        Subscription subscription = activeSubscriptions.remove(roomId);
        if (subscription != null) {
            try {
                listenerContainer.remove(subscription);
                log.info("Unsubscribed from room: {}", roomId);
            } catch (Exception e) {
                log.error("Failed to unsubscribe from room: {}", roomId, e);
            }
        }
    }

    @Override
    public void onMessage(ObjectRecord<String, ChatMessageRes> record) {
        try {
            ChatMessageRes message = record.getValue();
            
            // @Lazy로 지연 주입된 messagingTemplate 사용
            try {
                // WebSocket으로 메시지 브로드캐스트
                messagingTemplate.convertAndSend("/sub/rooms/" + message.roomId(), message);
                log.info("✅ Message sent to WebSocket: roomId={}, content={}", 
                        message.roomId(), message.content().substring(0, Math.min(20, message.content().length())));
            } catch (Exception e) {
                log.error("❌ Failed to send message to WebSocket: roomId={}, error={}", message.roomId(), e.getMessage());
                return; // ACK 하지 않고 나중에 다시 처리
            }
            
            // ACK 처리
            redisTemplate.opsForStream().acknowledge(record.getStream(), CONSUMER_GROUP, record.getId());
            
            log.debug("Message consumed and sent to WebSocket: roomId={}", message.roomId());
            
        } catch (Exception e) {
            log.error("Failed to process stream message: {}", record, e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (listenerContainer != null) {
            listenerContainer.stop();
        }
        activeSubscriptions.clear();
        log.info("Stream Consumer Manager cleaned up");
    }
}