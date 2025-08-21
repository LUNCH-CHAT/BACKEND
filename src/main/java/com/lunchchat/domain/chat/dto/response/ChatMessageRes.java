package com.lunchchat.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lunchchat.domain.chat.entity.ChatMessage;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.bson.types.ObjectId;

public record ChatMessageRes(
    @JsonProperty("id") MessageId messageId,
    Long roomId,
    Long senderId,
    String content,
    @JsonFormat(pattern = "yyyy.M.dd (E) HH:mm")
    LocalDateTime createdAt
){
    
    public record MessageId(
        long timestamp,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime date
    ) {
        public static MessageId from(ObjectId objectId) {
            return new MessageId(
                objectId.getTimestamp(),
                LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(objectId.getTimestamp()),
                    ZoneId.systemDefault()
                )
            );
        }
    }
    public static ChatMessageRes from(ChatMessage chatMessage) {
        return new ChatMessageRes(
            MessageId.from(chatMessage.getId()),
            chatMessage.getChatRoomId(),
            chatMessage.getSenderId(),
            chatMessage.getContent(),
            chatMessage.getSentAt()
        );
    }

    public static ChatMessageRes of(Long roomId, ChatMessage message) {
        return new ChatMessageRes(
            MessageId.from(message.getId()),
            roomId,
            message.getSenderId(),
            message.getContent(),
            message.getSentAt()
        );
    }
}
