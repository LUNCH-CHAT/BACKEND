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

    boolean isDeletedFromStarter = false;
    boolean isDeletedFromFriend = false;
    LocalDateTime lastMessageSendAt;

    public static ChatRoom of(Long starterId, Long friendId) {
        ChatRoom room = new ChatRoom();
        room.starterId = starterId;
        room.friendId = friendId;
        return room;
    }

    public void activateRoom() {
        this.isDeletedFromStarter = false;
        this.isDeletedFromFriend = false;
        this.lastMessageSendAt = LocalDateTime.now();
    }

    public void quit(Long userId) {
        if (this.getStarterId().equals(userId))
            this.isDeletedFromStarter = true;
        else this.isDeletedFromFriend = true;
    }
}
