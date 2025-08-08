package com.lunchchat.domain.chat.entity;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private ObjectId id;

    private Long chatRoomId; // RDB ChatRoom ID 참조
    private Long senderId;   // RDB Member ID 참조
    private String content;
    private Boolean isRead;
    private LocalDateTime sentAt;

    public static ChatMessage of(Long chatRoomId, Long senderId, String content) {
        ChatMessage message = new ChatMessage();
        message.chatRoomId = chatRoomId;
        message.senderId = senderId;
        message.content = content;
        message.isRead = false;
        message.sentAt = LocalDateTime.now();
        return message;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
