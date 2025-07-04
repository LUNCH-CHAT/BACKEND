package com.lunchchat.domain.notifications;

import com.lunchchat.domain.users.User;
import com.lunchchat.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isRead;
}
