package com.lunchchat.domain.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
//WebSocket 구성
//클라이언트가 웹소켓을 연결하기 위한 엔드포인트와 핸들러를 등록
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // STOMP 엔드포인트 설정
                .setAllowedOrigins("*") // CORS 설정, 실제 도메인으로 변경 필요
                .withSockJS(); // 브라우저가 웹소켓 지원하지 않을시 sockJs 방식으로 연결
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*");
    }

    // 메시지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //메시지를 받을 때 경로를 설정
        //해당 경로로 simpleBroker 등록
        //구독하는 클라이언트에게 메시지 전달
        registry.enableSimpleBroker("/sub");

        //메시지 전송 시 경로 설정 / send 요청 처리
        //클라이언트가 메시지를 보낼 때, 경로 앞에 /pub 있으면 broker로 보내짐
        registry.setApplicationDestinationPrefixes("/pub");
    }

}
