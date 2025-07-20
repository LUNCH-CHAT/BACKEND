package com.lunchchat.domain.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ChatRoomCardRes(
    Long roomId,
    String friendName,
    String lastMessage,
    @JsonFormat(pattern = "HH:mm")
    LocalDateTime lastMessageSentAt,
    int unreadCount
) {

}
