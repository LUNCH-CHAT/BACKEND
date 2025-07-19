package com.lunchchat.domain.chat.service;

import com.lunchchat.domain.chat.chat_message.entity.ChatMessage;
import com.lunchchat.domain.chat.chat_room.entity.ChatRoom;
import com.lunchchat.domain.chat.dto.request.CreateChatRoomReq;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import com.lunchchat.domain.chat.dto.response.ChatRoomCardRes;
import com.lunchchat.domain.chat.dto.response.CreateChatRoomRes;
import com.lunchchat.domain.chat.repository.ChatMessageRepository;
import com.lunchchat.domain.chat.repository.ChatRoomRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.ChatException;
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
    private final MemberRepository memberRepository;

    @Transactional
    public CreateChatRoomRes createRoom(CreateChatRoomReq req) {
        Long starterId = req.starterId();
        Long friendId = req.friendId();

        if (starterId.equals(friendId)) {
            throw new ChatException(ErrorStatus.CANNOT_CHAT_WITH_SELF);
        }

        Member starter = memberRepository.findById(starterId)
                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));

        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));

        // 기존 채팅방 존재하는지 확인 (starter-friend / friend-starter 모두 포함)
        Optional<ChatRoom> existingRoom = chatRoomRepository
                .findByStarterIdAndFriendId(starterId, friendId)
                .or(() -> chatRoomRepository.findByStarterIdAndFriendId(friendId, starterId));

        ChatRoom chatRoom = existingRoom.orElseGet(() -> {
            ChatRoom newRoom = ChatRoom.of(starter, friend);
            return chatRoomRepository.save(newRoom);
        });

        return new CreateChatRoomRes(
                chatRoom.getId(),
                chatRoom.getStarter().getId(),
                chatRoom.getFriend().getId()
        );
    }

    //채팅방 퇴장
    @Transactional
    public void exitRoom(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorStatus.CHATROOM_NOT_FOUND));

        //사용자 존재 확인
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));

        // 채팅방에 속한 사용자만 퇴장 가능
        if (!chatRoom.getStarter().equals(member) && !chatRoom.getFriend().equals(member)) {
            throw new ChatException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
        }

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

        // 유저 존재 검증
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));

        // 사용자 기준 채팅방 조회
        List<ChatRoom> rooms = chatRoomRepository.findAllByStarterOrFriend(user, user);

        List<ChatRoomCardRes> result = new ArrayList<>();

        for (ChatRoom room : rooms) {
            // 나간 채팅방은 제외
            if ((room.getStarter().equals(user) && room.isExitedByStarter()) ||
                    (room.getFriend().equals(user) && room.isExitedByFriend())) {
                continue;
            }

            // 최근 메시지 조회
            ChatMessage lastMessage = chatMessageRepository.findTop1ByChatRoomOrderByIdDesc(room)
                    .orElseThrow(() -> new ChatException(ErrorStatus.NO_MESSAGES_IN_CHATROOM));

            //상대방 정보
            Member friend = room.getStarter().equals(user) ? room.getFriend() : room.getStarter();
            String friendName = friend.getMembername();

            //안 읽은 메시지 수
            int unreadCount = chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(room, user);

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

    // 채팅방 내 채팅 메시지 조회(단일 채팅방 조회)
    @Transactional(readOnly = true)
    public List<ChatMessageRes> getChatMessages(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorStatus.CHATROOM_NOT_FOUND));

        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));

        // 해당 사용자가 채팅방에 속해있는지 확인
        if (!room.getStarter().equals(user) && !room.getFriend().equals(user)) {
            throw new ChatException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
        }

        //채팅방 퇴장 여부 확인

        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomOrderBySentAtAsc(room);

        messages.stream()
                .filter(msg -> !msg.getSender().equals(user)) // 상대방이 보낸 메시지
                .filter(msg -> !msg.getIsRead())              // 아직 읽지 않은 메시지
                .forEach(msg -> msg.markAsRead());            // 읽음 처리

        return messages.stream()
                .map(ChatMessageRes::from)
                .toList();
    }

}
