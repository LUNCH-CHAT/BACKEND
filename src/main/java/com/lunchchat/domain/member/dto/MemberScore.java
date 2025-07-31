package com.lunchchat.domain.member.dto;

import com.lunchchat.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberScore {
    private final Member member;
    private final int score;
}
