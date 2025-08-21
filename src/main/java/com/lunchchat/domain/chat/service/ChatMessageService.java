// src/main/java/com/lunchchat/domain/chat/service/ChatMessageService.java

package com.lunchchat.domain.chat.service;

import com.lunchchat.domain.chat.dto.request.ChatMessageReq;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import com.lunchchat.domain.chat.entity.ChatMessage;
import com.lunchchat.domain.chat.entity.ChatRoom;
import com.lunchchat.domain.chat.redis.RedisPublisher;
import com.lunchchat.domain.chat.repository.ChatMessageRepository;
import com.lunchchat.domain.chat.repository.ChatRoomRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final RedisPublisher redisPublisher;

    @Transactional
    public void sendMessage(Long roomId, String senderEmail, ChatMessageReq messageReq) {
        ChatRoom room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        Member sender = memberRepository.findByEmail(senderEmail)
            .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));
        Long senderId = sender.getId();

        if (!room.isParticipant(senderId)) {
            throw new ChatException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
        }

        ChatMessage message = chatMessageRepository.save(
            handleMessage(senderId, room, messageReq.content()));
        room.updateLastMessageSendAt(message.getSentAt());

        ChatMessageRes chatMessageRes = ChatMessageRes.of(roomId, message);

        //  수정된 부분: 채팅방별 고유 채널로 메시지를 발행합니다.
        redisPublisher.publishToRoom(roomId, chatMessageRes);
        log.info("🚀 Publishing message to Redis Pub/Sub: roomId={}, content={}",
            roomId,
            chatMessageRes.content().substring(0, Math.min(20, chatMessageRes.content().length())));
    }

    private ChatMessage handleMessage(Long senderId, ChatRoom room, String content) {
        Member sender = memberRepository.findById(senderId)
            .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));

        if (senderId.equals(room.getStarter().getId()) || senderId.equals(
            room.getFriend().getId())) {
            room.activateRoom();
            return ChatMessage.of(room.getId(), senderId, content);
        } else {
            throw new ChatException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
        }
    }
}