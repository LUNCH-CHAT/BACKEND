package com.lunchchat.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lunchchat.domain.chat.chat_message.entity.ChatMessage;
import java.time.LocalDateTime;

public record ChatMessageRes(
    Long id,
    Long roomId,
    Long senderId,
    String content,
    @JsonFormat(pattern = "yyyy.M.dd (E) HH:mm")
    LocalDateTime createdAt
){
    public static ChatMessageRes from(ChatMessage chatMessage) {
        return new ChatMessageRes(
            chatMessage.getId(),
            chatMessage.getChatRoom().getId(),
            chatMessage.getSenderId(),
            chatMessage.getContent(),
            chatMessage.getCreatedAt()
        );
    }

    public static ChatMessageRes of(Long roomId, ChatMessage message) {
        return new ChatMessageRes(
            message.getId(),
            roomId,
            message.getSenderId(),
            message.getContent(),
            message.getCreatedAt()
        );
    }
}
