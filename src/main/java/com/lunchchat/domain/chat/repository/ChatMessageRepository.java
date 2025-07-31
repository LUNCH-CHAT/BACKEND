package com.lunchchat.domain.chat.repository;

import com.lunchchat.domain.chat.entity.ChatMessage;
import com.lunchchat.domain.chat.entity.ChatRoom;
import com.lunchchat.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findTop1ByChatRoomOrderByIdDesc(ChatRoom room);

    int countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, Member sender);

    List<ChatMessage> findAllByChatRoomOrderBySentAtAsc(ChatRoom room);

    void deleteByChatRoom(ChatRoom chatRoom);

}
