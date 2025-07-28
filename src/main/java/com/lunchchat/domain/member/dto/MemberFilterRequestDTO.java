package com.lunchchat.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberFilterRequestDTO {

    @NotNull
    @Schema(description = "페이지 크기", example = "10")
    private Integer size;

    @NotNull
    @Schema(description = "페이지 번호 (0부터 시작)", example = "0")
    private Integer page;

    @Schema(description = "관심사 (EXCHANGE_STUDENT 등), 아무것도 없을 시 전체선택", example = "EXCHANGE_STUDENT")
    private String interest;

    @NotNull
    @Schema(
            description = "정렬 방식 (recent = 최신순, recommend = 추천순)",
            allowableValues = {"recent", "recommend"},
            example = "recommend"
    )
    private String sort;

    @Schema(description = "단과대 이름", example = "인문과학대학")
    private String college;

    @Schema(description = "학과 이름", example = "국어국문학과")
    private String department;

    @Schema(description = "학번 ('21학번', '20학번이상')", example = "21학번")
    private String studentNo;
}


