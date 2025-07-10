package com.lunchchat.domain.chat.dto.request;

public record ChatMessageReq(
    Long roomId,
    Long senderId,
    String content
) { }
