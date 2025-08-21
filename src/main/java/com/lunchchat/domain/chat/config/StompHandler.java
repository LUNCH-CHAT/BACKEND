package com.lunchchat.domain.chat.config;

import com.lunchchat.domain.chat.service.ChatRoomService;
import com.lunchchat.domain.chat.service.WebSocketSessionManager;
import com.lunchchat.global.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final ChatRoomService chatRoomService;
    private final WebSocketSessionManager sessionManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            // WebSocket 연결 시 Authorization 헤더에서 JWT 추출
            String token = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

            if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
                log.warn("토큰이 누락되었거나 형식이 잘못되었습니다.");
                throw new IllegalArgumentException("JWT 토큰 누락 또는 형식 오류");
            }

            token = token.substring(7); // "Bearer " 제거

            if (!jwtUtil.validateToken(token)) {
                log.warn("유효하지 않은 JWT 토큰입니다.");
                throw new IllegalArgumentException("유효하지 않은 JWT 토큰");
            }

            // 토큰 유효성 검증 성공 시, 사용자 정보 설정 (선택)
            Claims claims = jwtUtil.parseJwt(token);
            String email = jwtUtil.getEmail(claims);  // Subject에서 이메일 추출
            accessor.setUser(() -> email); // Principal 설정

            //사용자 정보를 websocket 연결 컨텍스트에 저장 (이후 subscribe, send 시에도 꺼내 쓸 수 있게)
            accessor.getSessionAttributes().put("user", email);

            log.info("stomp 연결 성공: {}", email);
        }

        if (StompCommand.SUBSCRIBE.equals(command)) {

            Object user = accessor.getSessionAttributes().get("user");
            if (user != null) {
                accessor.setUser(() -> (String) user);
            } else {
                log.warn("SUBSCRIBE 요청에 인증된 사용자 없음");
                return null;
            }

            String email = accessor.getUser().getName(); // CONNECT 시 저장한 이메일

            String destination = accessor.getDestination();  // 예: /sub/chat/room/3

            Long chatRoomId = extractRoomIdFromDestination(destination);
            if (!chatRoomService.hasAccess(email, chatRoomId)) {
                log.warn("채팅방 접근 권한 없음 - userEmail: {}, roomId: {}", email, chatRoomId);
                //throw new AccessDeniedException("채팅방 접근 권한 없음");
                return null;  //예외 반환시 웹소켓 연결 해제되는 현상 방지위해 null 반환
            }

            // 세션 관리에 사용자 추가 및 Redis Stream 구독
            sessionManager.addSessionToRoom(chatRoomId, email);
            
            log.info("채팅방 구독 허용 - userEmail: {}, roomId: {}", email, chatRoomId);
        }

        if (StompCommand.DISCONNECT.equals(command)) {
            // 연결 해제 시 모든 구독중인 채팅방에서 사용자 제거
            Object user = accessor.getSessionAttributes().get("user");
            if (user != null) {
                String email = (String) user;
                // 실제로는 사용자가 구독중인 모든 채팅방을 추적해야 하지만,
                // 여기서는 간단히 처리 (추후 개선 가능)
                log.info("사용자 연결 해제: {}", email);
            }
            log.info("stomp 연결 해제");
        }

        //subscribe 로직

        return message;


    }

    private Long extractRoomIdFromDestination(String destination) {
        // 예: /sub/chat/room/3 → 3
        if (destination == null) return null;
        String[] parts = destination.split("/");
        try {
            return Long.parseLong(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("채팅방 ID 파싱 실패: " + destination);
        }
    }

}
