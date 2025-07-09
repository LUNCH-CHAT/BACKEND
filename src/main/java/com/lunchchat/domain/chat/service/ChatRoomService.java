package com.lunchchat.domain.chat.service;

import com.lunchchat.domain.chat.chat_room.entity.ChatRoom;
import com.lunchchat.domain.chat.dto.request.CreateChatRoomReq;
import com.lunchchat.domain.chat.dto.response.CreateChatRoomRes;
import com.lunchchat.domain.chat.repository.ChatRoomRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
//    private final MemberRepository memberRepository;

    @Transactional
    public CreateChatRoomRes createRoom(CreateChatRoomReq req) {
        Long starterId = req.starterId();
        Long friendId = req.friendId();

        if (starterId.equals(friendId)) {
            throw new IllegalArgumentException("자기 자신과는 채팅할 수 없습니다.");
        }

//        유저 구현시 검증 예정
//        Member starter = memberRepository.findById(starterId)
//                .orElseThrow(() -> new NoSuchElementException("Starter not found"));
//        Member friend = memberRepository.findById(friendId)
//                .orElseThrow(() -> new NoSuchElementException("Friend not found"));

        // 기존 채팅방 존재하는지 확인 (starter-friend / friend-starter 모두 포함)
        Optional<ChatRoom> existingRoom = chatRoomRepository
                .findByStarterAndFriend(starterId, friendId)
                .or(() -> chatRoomRepository.findByStarterAndFriend(friendId, starterId));

        ChatRoom chatRoom = existingRoom.orElseGet(() -> {
            ChatRoom newRoom = ChatRoom.of(starterId, friendId);
            return chatRoomRepository.save(newRoom);
        });

        return new CreateChatRoomRes(
                chatRoom.getId(),
                chatRoom.getStarterId(),
                chatRoom.getFriendId()
        );
    }

}
