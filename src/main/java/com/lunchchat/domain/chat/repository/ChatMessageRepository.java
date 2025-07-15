package com.lunchchat.domain.chat.repository;

import com.lunchchat.domain.chat.chat_message.entity.ChatMessage;
import com.lunchchat.domain.chat.chat_room.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findTop1ByChatRoomOrderByIdDesc(ChatRoom room);

    int countByChatRoomAndSenderIdNotAndIsReadFalse(ChatRoom room, Long userId);

    List<ChatMessage> findAllByChatRoomOrderBySentAtAsc(ChatRoom room);

    void deleteByChatRoom(ChatRoom chatRoom);

    // Custom query methods can be defined here if needed
    // For example, to find messages by chat room ID or user ID

}
