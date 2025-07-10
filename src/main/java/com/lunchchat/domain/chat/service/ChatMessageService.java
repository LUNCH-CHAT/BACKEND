package com.lunchchat.domain.chat.service;

import com.lunchchat.domain.chat.chat_message.entity.ChatMessage;
import com.lunchchat.domain.chat.chat_room.entity.ChatRoom;
import com.lunchchat.domain.chat.dto.request.ChatMessageReq;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import com.lunchchat.domain.chat.repository.ChatMessageRepository;
import com.lunchchat.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    // 메시지 전송 로직 구현

    public void sendMessage(Long roomId, ChatMessageReq messageReq) {

        //user, room 불러오기 -> 유저 구현시 직접 참조로 변경
        Long senderId = messageReq.senderId();
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        ChatMessage message = chatMessageRepository.save(handleMessage(senderId, room, messageReq.content()));

        ChatMessageRes chatMessageRes = ChatMessageRes.of(roomId, message);

        messagingTemplate.convertAndSend("/sub/rooms/" + roomId, chatMessageRes);

        //알림 로직 구현시 추가
    }

    private ChatMessage handleMessage(Long senderId, ChatRoom room, String content) {

        if (senderId.equals(room.getStarterId())) {
            room.activateRoom();
            return ChatMessage.of(room, senderId, content);
        } else if (senderId.equals(room.getFriendId())) {
            room.activateRoom();
            return ChatMessage.of(room, senderId, content);
        } else
        {
            throw new IllegalArgumentException("Invalid sender ID");
        }
    }
}
