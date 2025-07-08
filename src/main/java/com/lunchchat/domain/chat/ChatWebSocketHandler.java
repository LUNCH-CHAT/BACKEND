package com.lunchchat.domain.chat;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


//WebSocket 연결 이후 연결을 처리하는 핸들러
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> clientSession = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        // 메시지 처리 로직
        session.sendMessage(new TextMessage("서버에서 보내는 응답: " + payload));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결 설정 후 로직
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결 종료 후 로직
    }
}
