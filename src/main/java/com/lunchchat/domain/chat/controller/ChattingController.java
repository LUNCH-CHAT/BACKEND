package com.lunchchat.domain.chat.controller;

import com.lunchchat.domain.chat.dto.request.ChatMessageReq;
import com.lunchchat.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChattingController {

    private final ChatMessageService chatMessageService;

    // 클라이언트로부터 채팅 메시지 받는 엔드포인트
    // prefix를 "/pub"로 설정했으므로, 실제 메시지는 "/pub/chat/{roomId}"로 전송됨
    @MessageMapping("/chat/{roomId}")
    public void handleMessage(@DestinationVariable Long roomId, ChatMessageReq chatMessageReq) {
        // 채팅 메시지 전송
        chatMessageService.sendMessage(roomId, chatMessageReq);
    }

}
