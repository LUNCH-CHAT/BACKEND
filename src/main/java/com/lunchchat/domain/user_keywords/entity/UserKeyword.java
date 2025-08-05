package com.lunchchat.domain.user_keywords.entity;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeywordType type;

    @Column(nullable = true, length = 100)
    private String title;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String description;

    public void updateTitle(String title) { this.title = title; }
    public void updateDescription(String description) { this.description = description; }
}
