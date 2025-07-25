package com.lunchchat.domain.chat.config;

import com.lunchchat.domain.chat.service.ChatRoomService;
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
//    private final ChatRoomService chatRoomService;

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
//            Claims claims = jwtUtil.parseJwt(token);
//            accessor.setUser(() -> claims.getSubject());  // principal 설정 (ex: email 또는 userId)
        }

        //subscribe 로직

        log.info("stomp 인증 성공");

        return message;
    }
}
