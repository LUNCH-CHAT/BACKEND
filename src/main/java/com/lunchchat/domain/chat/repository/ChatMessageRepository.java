package com.lunchchat.domain.chat.repository;

import com.lunchchat.domain.chat.chat_message.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Custom query methods can be defined here if needed
    // For example, to find messages by chat room ID or user ID

}
