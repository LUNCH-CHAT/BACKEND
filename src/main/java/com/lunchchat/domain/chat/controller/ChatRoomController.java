package com.lunchchat.domain.chat.controller;

import com.lunchchat.domain.chat.dto.request.CreateChatRoomReq;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import com.lunchchat.domain.chat.dto.response.ChatRoomCardRes;
import com.lunchchat.domain.chat.dto.response.CreateChatRoomRes;
import com.lunchchat.domain.chat.service.ChatRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    //채팅방 리스트 조회
    @GetMapping
    public ResponseEntity<List<ChatRoomCardRes>> getChatRooms(@RequestParam("userId") Long userId) {
        List<ChatRoomCardRes> rooms = chatRoomService.getChatRooms(userId);
        return ResponseEntity.ok(rooms);
    }

    //채팅방 퇴장
    @PatchMapping("/{roomId}")
    public ResponseEntity<Void> exitChatRoom(@PathVariable Long roomId, @RequestParam("userId") Long userId) {
        chatRoomService.exitRoom(roomId, userId);
        return ResponseEntity.noContent().build();
    }

    // 채팅방 내 메시지 전체 조회
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageRes>> getChatMessages(@PathVariable Long roomId, @RequestParam("userId") Long userId) {
        List<ChatMessageRes> messages = chatRoomService.getChatMessages(roomId, userId);
        return ResponseEntity.ok(messages);
    }
}
