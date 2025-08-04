package com.lunchchat.domain.chat.repository;

import com.lunchchat.domain.chat.entity.ChatRoom;
import com.lunchchat.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByStarterIdAndFriendId(Long starterId, Long friendId);

    List<ChatRoom> findAllByStarterOrFriend(Member starter, Member friend);

    @Query("""
        SELECT r FROM ChatRoom r
        WHERE (r.starter = :user OR r.friend = :user)
        AND (:cursor IS NULL OR r.lastMessageSendAt < :cursor)
        ORDER BY r.lastMessageSendAt DESC
        """)
    List<ChatRoom> findChatRoomsByCursor(
            @Param("user") Member user,
            @Param("cursor") LocalDateTime cursor,
            Pageable pageable
    );

    @Query("""
    SELECT c FROM ChatRoom c
    WHERE (c.starter = :user OR c.friend = :user)
    AND (
        :cursorTime IS NULL OR
        (c.lastMessageSendAt < :cursorTime) OR
        (c.lastMessageSendAt = :cursorTime AND c.id < :cursorId)
    )
    ORDER BY c.lastMessageSendAt DESC, c.id DESC
""")
    List<ChatRoom> findByUserWithCursor(Member user, LocalDateTime cursorTime, Long cursorId, Pageable pageable);
}
