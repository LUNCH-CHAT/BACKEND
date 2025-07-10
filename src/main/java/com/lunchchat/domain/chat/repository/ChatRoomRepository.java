package com.lunchchat.domain.chat.repository;

import com.lunchchat.domain.chat.chat_room.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByStarterIdAndFriendId(Long starterId, Long friendId);

    // Custom query methods can be defined here if needed
    // For example, to find chat rooms by user ID or other criteria

}
