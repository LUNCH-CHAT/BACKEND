package com.lunchchat.domain.chat.service;

import com.lunchchat.domain.chat.chat_message.entity.ChatMessage;
import com.lunchchat.domain.chat.chat_room.entity.ChatRoom;
import com.lunchchat.domain.chat.dto.request.CreateChatRoomReq;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import com.lunchchat.domain.chat.dto.response.ChatRoomCardRes;
import com.lunchchat.domain.chat.dto.response.CreateChatRoomRes;
import com.lunchchat.domain.chat.repository.ChatMessageRepository;
import com.lunchchat.domain.chat.repository.ChatRoomRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.ChatException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
//    private final MemberRepository memberRepository;

    @Transactional
    public CreateChatRoomRes createRoom(CreateChatRoomReq req) {
        Long starterId = req.starterId();
        Long friendId = req.friendId();

        if (starterId.equals(friendId)) {
            throw new ChatException(ErrorStatus.CANNOT_CHAT_WITH_SELF);
        }

//        유저 구현시 검증 예정
//        Member starter = memberRepository.findById(starterId)
//                .orElseThrow(() -> new NoSuchElementException("Starter not found"));
//        Member friend = memberRepository.findById(friendId)
//                .orElseThrow(() -> new NoSuchElementException("Friend not found"));

        // 기존 채팅방 존재하는지 확인 (starter-friend / friend-starter 모두 포함)
        Optional<ChatRoom> existingRoom = chatRoomRepository
                .findByStarterIdAndFriendId(starterId, friendId)
                .or(() -> chatRoomRepository.findByStarterIdAndFriendId(friendId, starterId));

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

    //채팅방 퇴장
    @Transactional
    public void exitRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorStatus.CHATROOM_NOT_FOUND));

        //유저 구현시 유저 검증

        chatRoom.exit(userId);

        // 채팅방에서 양쪽 모두 퇴장했는지 확인
        if(chatRoom.isExitedByStarter() && chatRoom.isExitedByFriend()) {
            chatMessageRepository.deleteByChatRoom(chatRoom);   //관련 채팅 메시지 삭제
            chatRoomRepository.delete(chatRoom);    //채팅방 삭제
        }
    }

    //채팅방 리스트 조회
    @Transactional(readOnly = true)
    public List<ChatRoomCardRes> getChatRooms(Long userId) {
        List<ChatRoom> rooms = chatRoomRepository.findAllByStarterIdOrFriendId(userId, userId);

        // 메시지가 있는 방만 필터링
        List<ChatRoom> filteredRooms = rooms.stream()
                .filter(room -> chatMessageRepository.findTop1ByChatRoomOrderByIdDesc(room).isPresent())
                .toList();

        List<ChatRoomCardRes> result = new ArrayList<>();

        for (ChatRoom room : filteredRooms) {

            // 나간 채팅방은 제외
            if ((room.getStarterId().equals(userId) && room.isExitedByStarter()) ||
                    (room.getFriendId().equals(userId) && room.isExitedByFriend())) {
                continue;
            }

            ChatMessage lastMessage = chatMessageRepository.findTop1ByChatRoomOrderByIdDesc(room)
                    .orElseThrow(() -> new ChatException(ErrorStatus.NO_MESSAGES_IN_CHATROOM));

            Long friendId = room.getStarterId().equals(userId) ? room.getFriendId() : room.getStarterId();

            // TODO: 유저 로직 구현 시 친구 이름 조회
            String friendName = "친구 #" + friendId;

            int unreadCount = chatMessageRepository.countByChatRoomAndSenderIdNotAndIsReadFalse(room, userId);

            result.add(new ChatRoomCardRes(
                    room.getId(),
                    friendName,
                    lastMessage.getContent(),
                    lastMessage.getCreatedAt(),
                    unreadCount
            ));
        }

        return result;
    }

    // 채팅방 내 채팅 메시지 조회
    @Transactional(readOnly = true)
    public List<ChatMessageRes> getChatMessages(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorStatus.CHATROOM_NOT_FOUND));

        // 해당 사용자가 채팅방에 속해있는지 확인
        if (!room.getStarterId().equals(userId) && !room.getFriendId().equals(userId)) {
            throw new ChatException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
        }

        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomOrderBySentAtAsc(room);

        return messages.stream()
                .map(ChatMessageRes::from)
                .toList();
    }

}
