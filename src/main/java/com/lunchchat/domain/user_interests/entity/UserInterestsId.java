package com.lunchchat.domain.user_interests.entity;

import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode
public class UserInterestsId implements Serializable {
    private Long user;
    private Integer interests;
}
