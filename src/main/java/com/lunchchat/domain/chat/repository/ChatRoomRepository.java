package com.lunchchat.domain.chat.repository;

import com.lunchchat.domain.chat.chat_room.entity.ChatRoom;
import com.lunchchat.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByStarterIdAndFriendId(Long starterId, Long friendId);

    List<ChatRoom> findAllByStarterOrFriend(Member starter, Member friend);

}
