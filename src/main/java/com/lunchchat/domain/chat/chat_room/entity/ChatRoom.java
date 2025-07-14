package com.lunchchat.domain.chat.chat_room.entity;

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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "starter_id")
//    private Member starter;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "friend_id")
//    private Member friend;

    private Long starterId;

    private Long friendId;

    boolean isExitedByStarter = false;
    boolean isExitedByFriend = false;
    LocalDateTime lastMessageSendAt;

    public static ChatRoom of(Long starterId, Long friendId) {
        ChatRoom room = new ChatRoom();
        room.starterId = starterId;
        room.friendId = friendId;
        return room;
    }

    public void activateRoom() {
        this.isExitedByStarter = false;
        this.isExitedByFriend = false;
        this.lastMessageSendAt = LocalDateTime.now();
    }

    public void exit(Long userId) {
        if (this.getStarterId().equals(userId))
            this.isExitedByStarter = true;
        else if (this.getFriendId().equals(userId))
            this.isExitedByFriend = true;
        else
            throw new IllegalArgumentException("채팅방에 속한 사용자가 아닙니다. ");
    }
}
