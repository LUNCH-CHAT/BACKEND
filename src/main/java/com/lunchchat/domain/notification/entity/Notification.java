package com.lunchchat.domain.notification.entity;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member; // 알림을 받는 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender; // 알림을 보낸 사용자 (매칭 요청자 등)

    private String type; // "MATCH_REQUEST", "MATCH_ACCEPTED"

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isRead;

    public Notification(Member member, Member sender, String type, String content) {
        this.member = member;
        this.sender = sender;
        this.type = type;
        this.content = content;
        this.isRead = false;
    }
}
