package com.lunchchat.domain.user_keywords.dto;

import com.lunchchat.domain.user_keywords.entity.UserKeyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserKeywordDTO {
    private Integer id;
    private String type;
    private String title;
    private String description;

    public static UserKeywordDTO from(UserKeyword keyword) {
        return UserKeywordDTO.builder()
                .id(keyword.getId())
                .type(keyword.getType().name())
                .title(keyword.getTitle())
                .description(keyword.getDescription())
                .build();
    }
}

