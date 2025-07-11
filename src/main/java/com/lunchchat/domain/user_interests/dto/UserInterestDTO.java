package com.lunchchat.domain.user_interests.dto;

import com.lunchchat.domain.user_interests.entity.UserInterests;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserInterestDTO {
    private Integer id;
    private String name;

    public static UserInterestDTO from(UserInterests userInterest) {
        return UserInterestDTO.builder()
                .id(userInterest.getInterests().getId())
                .name(userInterest.getInterests().getName().toString())
                .build();
    }
}

