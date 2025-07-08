package com.lunchchat.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChattingController {

    //메시지 전송
//    @MessageMapping("/{roomId}")
//    public void sendMessage(@DestinationVariable Long roomId, MessageSendReq messageSendReq){
//
//    }

}
