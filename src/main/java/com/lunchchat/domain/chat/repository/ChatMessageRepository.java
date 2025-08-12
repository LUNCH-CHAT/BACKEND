package com.lunchchat.domain.chat.repository;

import com.lunchchat.domain.chat.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {
    // MongoDB에서는 기본적으로 ID로 조회하므로 별도의 메서드 정의가 필요하지 않습니다.
    // 추가적인 쿼리가 필요할 경우 여기에 메서드를 정의할 수 있습니다.

    // chatRoomId 로 메시지 삭제
    void deleteByChatRoomId(Long chatRoomId);

    // chatRoomId 로 메시지 조회
    List<ChatMessage> findByChatRoomId(Long chatRoomId);

    // 가장 최신 메시지 1개 가져오기 (chatRoomId 기준, sentAt 기준 내림차순)
    Optional<ChatMessage> findTopByChatRoomIdOrderBySentAtDesc(Long chatRoomId);

    // 읽지 않은 메시지 수 조회 (chatRoomId, senderId 제외, isRead=false)
    int countByChatRoomIdAndSenderIdNotAndIsReadFalse(Long chatRoomId, Long senderId);

    List<ChatMessage> findByChatRoomIdAndSentAtLessThanEqualAndIdLessThanOrderBySentAtDescIdDesc(
            Long chatRoomId, LocalDateTime sentAt, ObjectId id, Pageable pageable);

    List<ChatMessage> findByChatRoomIdOrderBySentAtDescIdDesc(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findByChatRoomIdAndSentAtGreaterThanEqualAndIdGreaterThanOrderBySentAtAscIdAsc(
            Long chatRoomId, LocalDateTime sentAt, ObjectId id, Pageable pageable);

    List<ChatMessage> findByChatRoomIdOrderBySentAtAscIdAsc(Long chatRoomId, Pageable pageable);


}
