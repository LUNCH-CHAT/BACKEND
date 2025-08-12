package com.lunchchat.domain.chat.dto.response;

public record CreateChatRoomRes(
    Long chatRoomId,
    Long starterId,
    String friendName,
    String friendDepartment
) { }
