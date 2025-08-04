package com.lunchchat.domain.chat.controller;

import com.lunchchat.domain.chat.dto.request.CreateChatRoomReq;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import com.lunchchat.domain.chat.dto.response.ChatRoomCardRes;
import com.lunchchat.domain.chat.dto.response.CreateChatRoomRes;
import com.lunchchat.domain.chat.service.ChatRoomService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.CursorPaginatedResponse;
import com.lunchchat.global.apiPayLoad.PaginatedResponse;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ApiResponse<CreateChatRoomRes>> createChatRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long friendId) {
        Long starterId = userDetails.getMemberId();

        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoomService.createRoom(starterId, friendId)));
    }

    //채팅방 리스트 조회
//    @GetMapping
//    @Operation(summary = "채팅방 리스트 조회")
//    public ResponseEntity<ApiResponse<List<ChatRoomCardRes>>> getChatRooms(@AuthenticationPrincipal
//            CustomUserDetails userDetails) {
//        Long userId = userDetails.getMemberId();
//
//        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoomService.getChatRooms(userId)));
//    }
    @GetMapping
    @Operation(summary = "채팅방 목록 조회 (lastMessageSendAt 기준 커서 페이징)")
    public ResponseEntity<ApiResponse<CursorPaginatedResponse<ChatRoomCardRes>>> getChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String cursor) {

        Long userId = userDetails.getMemberId();

        CursorPaginatedResponse<ChatRoomCardRes> response = chatRoomService.getChatRooms(userId, size, cursor);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }


    //채팅방 퇴장
    @PatchMapping("/{roomId}")
    @Operation(summary = "채팅방 퇴장")
    public ResponseEntity<ApiResponse<Void>> exitChatRoom(@PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getMemberId();
        chatRoomService.exitRoom(roomId, userId);

        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 채팅방 내 메시지 전체 조회(단일 채팅방 조회)
    @GetMapping("/{roomId}/messages")
    @Operation(summary = "채팅방 내 메시지 조회")
    public ResponseEntity<ApiResponse<List<ChatMessageRes>>> getChatMessages(@PathVariable Long roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getMemberId();

        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoomService.getChatMessages(roomId, userId)));
    }
}
