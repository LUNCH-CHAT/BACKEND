package com.lunchchat.domain.chat.controller;

import com.lunchchat.domain.chat.dto.request.CreateChatRoomReq;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import com.lunchchat.domain.chat.dto.response.ChatRoomCardRes;
import com.lunchchat.domain.chat.dto.response.CreateChatRoomRes;
import com.lunchchat.domain.chat.service.ChatRoomService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "채팅방 API")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //채팅방 생성
    @PostMapping
    @Operation(summary = "채팅방 생성")
    public ResponseEntity<ApiResponse<CreateChatRoomRes>> createChatRoom(@RequestBody CreateChatRoomReq req) {
        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoomService.createRoom(req)));
    }

    //채팅방 리스트 조회
    @GetMapping
    @Operation(summary = "채팅방 리스트 조회")
    public ResponseEntity<ApiResponse<List<ChatRoomCardRes>>> getChatRooms(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoomService.getChatRooms(userId)));
    }

    //채팅방 퇴장
    @PatchMapping("/{roomId}")
    @Operation(summary = "채팅방 퇴장")
    public ResponseEntity<ApiResponse<Void>> exitChatRoom(@PathVariable Long roomId, @RequestParam("userId") Long userId) {
        chatRoomService.exitRoom(roomId, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 채팅방 내 메시지 전체 조회
    @GetMapping("/{roomId}/messages")
    @Operation(summary = "채팅방 내 메시지 조회")
    public ResponseEntity<ApiResponse<List<ChatMessageRes>>> getChatMessages(@PathVariable Long roomId, @RequestParam("userId") Long userId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoomService.getChatMessages(roomId, userId)));
    }
}
