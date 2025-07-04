package com.lunchchat.domain.user_interests.entity;

import com.lunchchat.domain.interests.entity.Interests;
import com.lunchchat.domain.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(UserInterestsId.class)
public class UserInterests implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private Interests interests;
}
