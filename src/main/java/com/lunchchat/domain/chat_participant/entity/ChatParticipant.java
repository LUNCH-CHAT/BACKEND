package com.lunchchat.domain.chat_participant.entity;

import com.lunchchat.domain.chat_room.entity.ChatRoom;
import com.lunchchat.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ChatParticipantId.class)
public class ChatParticipant implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
