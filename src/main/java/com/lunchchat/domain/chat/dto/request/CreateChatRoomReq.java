package com.lunchchat.domain.chat.dto.request;

public record CreateChatRoomReq (
    Long starterId,
    Long friendId
){}
