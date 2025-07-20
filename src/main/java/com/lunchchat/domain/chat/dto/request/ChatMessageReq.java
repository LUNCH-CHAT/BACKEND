package com.lunchchat.domain.chat.dto.request;

public record ChatMessageReq(
    Long senderId,
    String content
) { }
