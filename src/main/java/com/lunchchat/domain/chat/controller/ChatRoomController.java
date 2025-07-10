package com.lunchchat.domain.chat.controller;

import com.lunchchat.domain.chat.dto.request.CreateChatRoomReq;
import com.lunchchat.domain.chat.dto.response.CreateChatRoomRes;
import com.lunchchat.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //채팅방 생성
    @PostMapping
    public ResponseEntity<CreateChatRoomRes> createChatRoom(@RequestBody CreateChatRoomReq req) {
        CreateChatRoomRes response = chatRoomService.createRoom(req);
        return ResponseEntity.ok(response);
    }

    //채팅방 입장

    //채팅방 리스트 조회

    //채팅방 퇴장


}
