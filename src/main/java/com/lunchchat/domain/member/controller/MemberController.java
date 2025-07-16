package com.lunchchat.domain.member.controller;

import com.lunchchat.domain.member.dto.MemberDetailResponseDTO;
import com.lunchchat.domain.member.dto.MemberRecommendationResponseDTO;
import com.lunchchat.domain.member.service.MemberQueryService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/{memberId}")
    @Operation(summary = "특정 사용자 프로필 상세 조회", description = "특정 사용자의 프로필을 상세 조회합니다.")
    public ApiResponse<MemberDetailResponseDTO> getMemberDetail(@PathVariable Long memberId) {
        MemberDetailResponseDTO detail = memberQueryService.getMemberDetail(memberId);
        return ApiResponse.onSuccess(detail);
    }

    @GetMapping("/recommendations")
    @Operation(summary = "시간표, 관심사가 겹치는 프로필 추천", description = "시간표 & 관심사 기준 추천 사용자들을 조회합니다.")
    public ApiResponse<List<MemberRecommendationResponseDTO>> getRecommendedMembers(@RequestParam Long currentMemberId) {
        return ApiResponse.onSuccess(memberQueryService.getRecommendedMembers(currentMemberId));
    }
}

