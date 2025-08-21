package com.lunchchat.domain.chat.service;

import com.lunchchat.domain.chat.redis.DynamicStreamConsumerManager;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketSessionManager {

    private final DynamicStreamConsumerManager consumerManager;
    
    // 서버별 활성 채팅방과 연결된 사용자 수 관리
    private final Map<Long, Set<String>> roomSessions = new ConcurrentHashMap<>();

    public void addSessionToRoom(Long roomId, String userEmail) {
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userEmail);
        
        // 첫 번째 사용자가 입장할 때 Redis Stream 구독 시작
        if (roomSessions.get(roomId).size() == 1) {
            log.info("🎯 Starting Redis Stream subscription for room: {}", roomId);
            consumerManager.subscribeToRoom(roomId);
            log.info("✅ Started Redis Stream subscription for room: {}", roomId);
        }
        
        log.debug("User {} joined room {}. Total users: {}", 
                userEmail, roomId, roomSessions.get(roomId).size());
    }

    public void removeSessionFromRoom(Long roomId, String userEmail) {
        Set<String> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(userEmail);
            
            // 마지막 사용자가 나갈 때 Redis Stream 구독 해제
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
                consumerManager.unsubscribeFromRoom(roomId);
                log.info("Stopped Redis Stream subscription for room: {}", roomId);
            }
            
            log.debug("User {} left room {}. Remaining users: {}", 
                    userEmail, roomId, sessions.size());
        }
    }

    public boolean hasActiveUsers(Long roomId) {
        Set<String> sessions = roomSessions.get(roomId);
        return sessions != null && !sessions.isEmpty();
    }

    public int getActiveUserCount(Long roomId) {
        Set<String> sessions = roomSessions.get(roomId);
        return sessions != null ? sessions.size() : 0;
    }
}