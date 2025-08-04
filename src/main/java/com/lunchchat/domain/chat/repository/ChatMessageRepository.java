package com.lunchchat.domain.chat.repository;

import com.lunchchat.domain.chat.entity.ChatMessage;
import com.lunchchat.domain.chat.entity.ChatRoom;
import com.lunchchat.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findTop1ByChatRoomOrderByIdDesc(ChatRoom room);

    int countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, Member sender);

    List<ChatMessage> findAllByChatRoomOrderBySentAtAsc(ChatRoom room);

    void deleteByChatRoom(ChatRoom chatRoom);

    @Query("""
    SELECT m FROM ChatMessage m
    WHERE m.chatRoom = :chatRoom
    AND (:cursorTime IS NULL OR 
         (m.sentAt < :cursorTime OR 
          (m.sentAt = :cursorTime AND m.id < :cursorId)))
    ORDER BY m.sentAt ASC, m.id ASC
    """)
    List<ChatMessage> findByChatRoomWithCursor(
            @Param("chatRoom") ChatRoom chatRoom,
            @Param("cursorTime") LocalDateTime cursorTime,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );
}
