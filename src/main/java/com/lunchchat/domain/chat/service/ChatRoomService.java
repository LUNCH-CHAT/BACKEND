package com.lunchchat.domain.chat.service;

import com.lunchchat.domain.chat.entity.ChatMessage;
import com.lunchchat.domain.chat.entity.ChatRoom;
import com.lunchchat.domain.chat.dto.response.ChatMessageRes;
import com.lunchchat.domain.chat.dto.response.ChatRoomCardRes;
import com.lunchchat.domain.chat.dto.response.CreateChatRoomRes;
import com.lunchchat.domain.chat.repository.ChatRoomRepository;
import com.lunchchat.domain.chat.repository.ChatMessageRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.CursorPaginatedResponse;
import com.lunchchat.global.apiPayLoad.PaginatedResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.ChatException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public CreateChatRoomRes createRoom(Long starterId, Long friendId) {

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
                chatRoom.getFriend().getMembername(),
                chatRoom.getFriend().getDepartment().getName()
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
        if (!chatRoom.getStarter().getId().equals(userId) && !chatRoom.getFriend().getId().equals(userId)) {
            throw new ChatException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
        }

        chatRoom.exit(userId);

        // 채팅방에서 양쪽 모두 퇴장했는지 확인
        if(chatRoom.isExitedByStarter() && chatRoom.isExitedByFriend()) {
            chatMessageRepository.deleteByChatRoomId(roomId);   //관련 채팅 메시지 삭제
            chatRoomRepository.delete(chatRoom);    //채팅방 삭제
        }
    }

    //채팅방 리스트 조회
//    @Transactional(readOnly = true)
//    public PaginatedResponse<ChatRoomCardRes> getChatRooms(Long userId, int page, int size) {
//        Member user = memberRepository.findById(userId)
//                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        // 나간 채팅방 필터링 쿼리 호출 (이제 여기서 걸러진 상태)
//        Page<ChatRoom> roomsPage = chatRoomRepository.findActiveChatRoomsByUser(user, pageable);
//
//        List<ChatRoomCardRes> data = roomsPage.stream()
//                .map(room -> {
//                    Long chatRoomId = room.getId();
//                    ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomIdOrderBySentAtDesc(chatRoomId)
//                            .orElse(null);
//
//                    String lastMessageContent = lastMessage != null ? lastMessage.getContent() : null;
//                    LocalDateTime lastMessageSentAt = lastMessage != null ? lastMessage.getSentAt() : null;
//
//                    Member friend = room.getStarter().equals(user) ? room.getFriend() : room.getStarter();
//                    String department = friend.getDepartment().getName();
//                    String friendName = friend.getMembername();
//
//                    int unreadCount = chatMessageRepository.countByChatRoomIdAndSenderIdNotAndIsReadFalse(chatRoomId, userId);
//
//                    return new ChatRoomCardRes(
//                            room.getId(),
//                            friendName,
//                            department,
//                            lastMessageContent,
//                            lastMessageSentAt,
//                            unreadCount
//                    );
//                })
//                .toList();
//
//        PaginatedResponse.Meta meta = PaginatedResponse.Meta.builder()
//                .currentPage(roomsPage.getNumber())
//                .pageSize(roomsPage.getSize())
//                .totalItems(roomsPage.getTotalElements())
//                .totalPages(roomsPage.getTotalPages())
//                .hasNext(roomsPage.hasNext())
//                .build();
//
//        return PaginatedResponse.<ChatRoomCardRes>builder()
//                .data(data)
//                .meta(meta)
//                .build();
//    }

    @Transactional(readOnly = true)
    public PaginatedResponse<ChatRoomCardRes> getChatRooms(Long userId, int page, int size) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatRoom> roomsPage = chatRoomRepository.findActiveChatRoomsByUser(user, pageable);

        List<Long> chatRoomIds = roomsPage.stream()
                .map(ChatRoom::getId)
                .toList();

        // Batch fetch last messages per chatRoomId using MongoDB aggregation
        Map<Long, ChatMessage> lastMessageMap = getLastMessages(chatRoomIds);

        // Batch fetch unread message counts per chatRoomId using aggregation
        Map<Long, Integer> unreadCountMap = getUnreadCounts(chatRoomIds, userId);

        List<ChatRoomCardRes> data = roomsPage.stream()
                .map(room -> {
                    Long chatRoomId = room.getId();

                    ChatMessage lastMessage = lastMessageMap.get(chatRoomId);
                    String lastMessageContent = lastMessage != null ? lastMessage.getContent() : null;
                    LocalDateTime lastMessageSentAt = lastMessage != null ? lastMessage.getSentAt() : null;

                    Member friend = room.getStarter().equals(user) ? room.getFriend() : room.getStarter();
                    String department = friend.getDepartment().getName();
                    String friendName = friend.getMembername();

                    int unreadCount = unreadCountMap.getOrDefault(chatRoomId, 0);

                    return new ChatRoomCardRes(
                            chatRoomId,
                            friendName,
                            department,
                            lastMessageContent,
                            lastMessageSentAt,
                            unreadCount
                    );
                })
                .toList();

        PaginatedResponse.Meta meta = PaginatedResponse.Meta.builder()
                .currentPage(roomsPage.getNumber())
                .pageSize(roomsPage.getSize())
                .totalItems(roomsPage.getTotalElements())
                .totalPages(roomsPage.getTotalPages())
                .hasNext(roomsPage.hasNext())
                .build();

        return PaginatedResponse.<ChatRoomCardRes>builder()
                .data(data)
                .meta(meta)
                .build();
    }

    private Map<Long, ChatMessage> getLastMessages(List<Long> chatRoomIds) {
        if (chatRoomIds.isEmpty()) return Collections.emptyMap();

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("chatRoomId").in(chatRoomIds)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "sentAt")),
                Aggregation.group("chatRoomId").first(Aggregation.ROOT).as("lastMessage")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "chat_messages", Document.class);

        Map<Long, ChatMessage> lastMessageMap = new HashMap<>();
        for (Document doc : results) {
            Long chatRoomId = doc.getLong("_id");
            Document lastMessageDoc = (Document) doc.get("lastMessage");
            ChatMessage lastMessage = mongoTemplate.getConverter().read(ChatMessage.class, lastMessageDoc);
            lastMessageMap.put(chatRoomId, lastMessage);
        }
        return lastMessageMap;
    }

    private Map<Long, Integer> getUnreadCounts(List<Long> chatRoomIds, Long userId) {
        if (chatRoomIds.isEmpty()) return Collections.emptyMap();

        Criteria criteria = Criteria.where("chatRoomId").in(chatRoomIds)
                .and("senderId").ne(userId)
                .and("isRead").is(false);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("chatRoomId").count().as("unreadCount")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "chat_messages", Document.class);

        Map<Long, Integer> unreadCountMap = new HashMap<>();
        for (Document doc : results) {
            Long chatRoomId = doc.getLong("_id");
            Integer unreadCount = doc.getInteger("unreadCount", 0);
            unreadCountMap.put(chatRoomId, unreadCount);
        }
        return unreadCountMap;
    }




//    @Transactional(readOnly = true)
//    public CursorPaginatedResponse<ChatRoomCardRes> getChatRooms(Long userId, int size, String cursor) {
//        Member user = memberRepository.findById(userId)
//                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));
//
//        LocalDateTime cursorTime = null;
//
//        // 커서 파싱 (예: "2025-08-04T21:33:12.345")
//        if (cursor != null && !cursor.isBlank()) {
//            try {
//                cursorTime = LocalDateTime.parse(cursor);
//            } catch (Exception e) {
//                throw new ChatException(ErrorStatus.INVALID_CURSOR_FORMAT);
//            }
//        }
//
//        Pageable pageable = PageRequest.of(
//                0,
//                size + 1,
//                Sort.by(Sort.Direction.DESC, "lastMessageSendAt") // 최신순 정렬
//        );
//
//        List<ChatRoom> rooms = chatRoomRepository.findByUserWithCursor(user, cursorTime, pageable);
//
//        boolean hasNext = rooms.size() > size;
//        if (hasNext) {
//            rooms = rooms.subList(0, size);
//        }
//
//        List<ChatRoomCardRes> result = new ArrayList<>();
//
//        for (ChatRoom room : rooms) {
//            // 퇴장한 유저의 채팅방 제외
//            if ((room.getStarter().getId().equals(userId) && room.isExitedByStarter()) ||
//                    (room.getFriend().getId().equals(userId) && room.isExitedByFriend())) {
//                continue;
//            }
//
//            ChatMessage lastMessage = chatMessageRepository.findTop1ByChatRoomOrderByIdDesc(room)
//                    .orElseThrow(() -> new ChatException(ErrorStatus.NO_MESSAGES_IN_CHATROOM));
//
//            Member friend = room.getStarter().equals(user) ? room.getFriend() : room.getStarter();
//            int unreadCount = chatMessageRepository.countByChatRoomAndSenderNotAndIsReadFalse(room, user);
//
//            result.add(new ChatRoomCardRes(
//                    room.getId(),
//                    friend.getMembername(),
//                    friend.getDepartment().getName(),
//                    lastMessage.getContent(),
//                    lastMessage.getCreatedAt(),
//                    unreadCount
//            ));
//        }
//
//        // 다음 커서 생성
//        String nextCursor = null;
//        if (hasNext && !rooms.isEmpty()) {
//            ChatRoom lastRoom = rooms.get(rooms.size() - 1);
//            nextCursor = lastRoom.getLastMessageSendAt().toString(); // ex: "2025-08-04T22:00:00.000"
//        }
//
//        return CursorPaginatedResponse.<ChatRoomCardRes>builder()
//                .data(result)
//                .meta(CursorPaginatedResponse.CursorMeta.builder()
//                        .pageSize(size)
//                        .hasNext(hasNext)
//                        .nextCursor(nextCursor)
//                        .build())
//                .build();
//    }


    // 채팅방 내 채팅 메시지 조회(단일 채팅방 조회)
//    @Transactional
//    public List<ChatMessageRes> getChatMessages(Long roomId, Long userId) {
//        ChatRoom room = chatRoomRepository.findById(roomId)
//                .orElseThrow(() -> new ChatException(ErrorStatus.CHATROOM_NOT_FOUND));
//
//        Member user = memberRepository.findById(userId)
//                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));
//
//        // 해당 사용자가 채팅방에 속해있는지 확인
//        if (!room.getStarter().getId().equals(userId) && !room.getFriend().getId().equals(userId)) {
//            throw new ChatException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
//        }
//
//        //채팅방 퇴장 여부 확인
//        if ((room.isExitedByStarter() && room.getStarter().getId().equals(userId)) ||
//                (room.isExitedByFriend() && room.getFriend().getId().equals(userId))) {
//            throw new ChatException(ErrorStatus.CHATROOM_EXITED);
//        }
//
//        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomOrderBySentAtAsc(room);
//
//        messages.stream()
//                .filter(msg -> !msg.getSender().getId().equals(userId)) // 상대방이 보낸 메시지
//                .filter(msg -> !msg.getIsRead())              // 아직 읽지 않은 메시지
//                .forEach(msg -> msg.markAsRead());            // 읽음 처리
//
//        return messages.stream()
//                .map(ChatMessageRes::from)
//                .toList();
//    }

    @Transactional
    public CursorPaginatedResponse<ChatMessageRes> getChatMessages(Long roomId, Long userId, int size, String cursor) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorStatus.CHATROOM_NOT_FOUND));

        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ErrorStatus.USER_NOT_FOUND));

        if (!room.getStarter().getId().equals(userId) && !room.getFriend().getId().equals(userId)) {
            throw new ChatException(ErrorStatus.UNAUTHORIZED_CHATROOM_ACCESS);
        }

        if ((room.isExitedByStarter() && room.getStarter().getId().equals(userId)) ||
                (room.isExitedByFriend() && room.getFriend().getId().equals(userId))) {
            throw new ChatException(ErrorStatus.CHATROOM_EXITED);
        }

        Pageable pageable = PageRequest.of(0, size + 1);

        List<ChatMessage> messages;

        if (cursor != null && !cursor.isBlank()) {
            try {
                String[] parts = cursor.split("_");
                LocalDateTime cursorTime = LocalDateTime.parse(parts[0]);
                ObjectId cursorId = new ObjectId(parts[1]);  // ObjectId 변환

                messages = chatMessageRepository.findByChatRoomIdAndSentAtLessThanEqualAndIdLessThanOrderBySentAtDescIdDesc(
                        room.getId(), cursorTime, cursorId, pageable);
            } catch (Exception e) {
                throw new ChatException(ErrorStatus.INVALID_CURSOR_FORMAT);
            }
        } else {
            messages = chatMessageRepository.findByChatRoomIdOrderBySentAtDescIdDesc(room.getId(), pageable);
        }

        boolean hasNext = messages.size() > size;
        if (hasNext) {
            messages = messages.subList(0, size);
        }

//        messages.stream()
//                .filter(msg -> !msg.getSenderId().equals(userId)) // MongoDB는 senderId 필드 사용
//                .filter(msg -> !msg.getIsRead())
//                .forEach(ChatMessage::markAsRead);

        //메시지 읽음 처리 bulk update
        List<ObjectId> unreadMsgIds = messages.stream()
                .filter(msg -> !msg.getSenderId().equals(userId))
                .filter(msg -> !msg.getIsRead())
                .map(ChatMessage::getId)
                .toList();

        if (!unreadMsgIds.isEmpty()) {
            Query query = new Query(Criteria.where("_id").in(unreadMsgIds));
            Update update = new Update().set("isRead", true);
            mongoTemplate.updateMulti(query, update, ChatMessage.class);
        }

        //db에 반영되지만, 클라이언트에도 일관된 응답 위해 in memory 객체에도 반영
        messages.stream()
                .filter(msg -> !msg.getSenderId().equals(userId))
                .filter(msg -> !Boolean.TRUE.equals(msg.getIsRead()))
                .forEach(ChatMessage::markAsRead);

        List<ChatMessageRes> result = messages.stream()
                .map(ChatMessageRes::from)
                .toList();

        String nextCursor = null;
        if (hasNext && !messages.isEmpty()) {
            ChatMessage last = messages.get(messages.size() - 1);
            nextCursor = last.getSentAt().toString() + "_" + last.getId().toHexString();
        }

        return CursorPaginatedResponse.<ChatMessageRes>builder()
                .userId(userId)
                .data(result)
                .meta(CursorPaginatedResponse.CursorMeta.builder()
                        .pageSize(size)
                        .hasNext(hasNext)
                        .nextCursor(nextCursor)
                        .build())
                .build();
    }

    //채팅방 접근 권한 확인(채팅방에 속한 사용자인지)
    @Transactional(readOnly = true)
    public boolean hasAccess(String email, Long chatRoomId) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ErrorStatus.CHATROOM_NOT_FOUND));

        // 사용자가 채팅방의 starter나 friend인지 확인
        boolean isMember = room.getStarter().getEmail().equals(email)
                || room.getFriend().getEmail().equals(email);

        // 퇴장한 경우 접근 불가
        boolean hasExited =
                (room.getStarter().getEmail().equals(email) && room.isExitedByStarter()) ||
                        (room.getFriend().getEmail().equals(email) && room.isExitedByFriend());

        return isMember && !hasExited;
    }

}
