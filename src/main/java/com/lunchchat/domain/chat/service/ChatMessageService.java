package com.lunchchat.domain.chat.service;

import com.lunchchat.domain.chat.entity.ChatMessage;
import com.lunchchat.domain.chat.entity.ChatRoom;
import com.lunchchat.domain.chat.dto.request.ChatMessageReq;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import com.lunchchat.domain.chat.repository.ChatMessageRepository;
import com.lunchchat.domain.chat.repository.ChatRoomRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.ChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
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

        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));

        if (senderId.equals(room.getStarter().getId()) || senderId.equals(room.getFriend().getId())) {
            room.activateRoom();
            return ChatMessage.of(room, sender, content);
        } else {
            throw new ChatException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
        }
    }
}
