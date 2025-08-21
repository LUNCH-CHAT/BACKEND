package com.lunchchat.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lunchchat.domain.chat.entity.ChatMessage;
import java.time.LocalDateTime;
import org.bson.types.ObjectId;

public record ChatMessageRes(
    String id,
    Long roomId,
    Long senderId,
    String content,
    @JsonFormat(pattern = "yyyy.M.dd (E) HH:mm")
    LocalDateTime createdAt
){
    public static ChatMessageRes from(ChatMessage chatMessage) {
        return new ChatMessageRes(
            chatMessage.getId().toString(),
            chatMessage.getChatRoomId(),
            chatMessage.getSenderId(),
            chatMessage.getContent(),
            chatMessage.getSentAt()
        );
    }

    public static ChatMessageRes of(Long roomId, ChatMessage message) {
        return new ChatMessageRes(
            message.getId().toString(),
            roomId,
            message.getSenderId(),
            message.getContent(),
            message.getSentAt()
        );
    }
}
