package com.lunchchat.domain.chat.entity;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "starter_id")
    private Member starter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private Member friend;

    boolean isExitedByStarter = false;
    boolean isExitedByFriend = false;
    LocalDateTime lastMessageSendAt;

    public static ChatRoom of(Member starter, Member friend) {
        ChatRoom room = new ChatRoom();
        room.starter = starter;
        room.friend = friend;
        room.lastMessageSendAt = LocalDateTime.now();
        return room;
    }

    public void activateRoom() {
        this.isExitedByStarter = false;
        this.isExitedByFriend = false;
        this.lastMessageSendAt = LocalDateTime.now();
    }

    public void exit(Long userId) {
        if (this.getStarter().getId().equals(userId))
            this.isExitedByStarter = true;
        else if (this.getFriend().getId().equals(userId))
            this.isExitedByFriend = true;
        else
            throw new IllegalArgumentException("채팅방에 속한 사용자가 아닙니다. ");
    }

    public boolean isParticipant(Long userId) {
        return starter.getId().equals(userId) || friend.getId().equals(userId);
    }

    public void setLastMessageSendAt(LocalDateTime sentAt) {
        this.lastMessageSendAt = sentAt;
    }
}
