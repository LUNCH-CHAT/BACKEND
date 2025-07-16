package com.lunchchat.domain.chat.chat_message.entity;

import com.lunchchat.domain.chat.chat_room.entity.ChatRoom;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isRead = false;

    private LocalDateTime sentAt;

    public static ChatMessage of(ChatRoom chatRoom, Member sender, String content) {
        ChatMessage message = new ChatMessage();
        message.chatRoom = chatRoom;
        message.sender = sender;
        message.content = content;
        message.isRead = false;
        message.sentAt = LocalDateTime.now();
        return message;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
