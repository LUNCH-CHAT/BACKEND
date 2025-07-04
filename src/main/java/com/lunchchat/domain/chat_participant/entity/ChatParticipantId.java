package com.lunchchat.domain.chat_participant.entity;

import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode
public class ChatParticipantId implements Serializable {
    private Long chatRoom;
    private Long user;
}
