package com.lunchchat.domain.member.controller;

import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.service.MemberCommandService;
import com.lunchchat.domain.member.service.MemberQueryService;
import com.lunchchat.domain.notification.dto.FcmUpdateRequestDto;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;

    @PatchMapping("/{memberId}/fcm-token")
    @Operation(summary = "FCM 토큰 업데이트", description = "사용자의 FCM 토큰을 업데이트합니다.")
    public ApiResponse<SuccessStatus> updateFcmToken(@PathVariable Long memberId,
        @RequestBody FcmUpdateRequestDto request) {
        memberCommandService.updateFcmToken(memberId, request.getFcmToken());
        return ApiResponse.of(SuccessStatus.FCM_TOKEN_UPDATE_SUCCESS, null);
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "특정 사용자 프로필 상세 조회", description = "특정 사용자의 프로필을 상세 조회합니다.")
    public ApiResponse<MemberResponseDTO.MemberDetailResponseDTO> getMemberDetail(
        @PathVariable Long memberId) {
        MemberResponseDTO.MemberDetailResponseDTO detail = memberQueryService.getMemberDetail(
            memberId);
        return ApiResponse.onSuccess(detail);
    }

    @GetMapping("/recommendations")
    @Operation(summary = "시간표, 관심사가 겹치는 프로필 추천", description = "시간표 & 관심사 기준 추천 사용자들을 조회합니다.")
    public ApiResponse<List<MemberResponseDTO.MemberRecommendationResponseDTO>> getRecommendedMembers(
        @RequestParam Long currentMemberId) {
        return ApiResponse.onSuccess(memberQueryService.getRecommendedMembers(currentMemberId));
    }

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회", description = "마이페이지를 조회합니다.")
    public ApiResponse<MemberResponseDTO.MyPageResponseDTO> getMyPageInfo(Member member) {
        // TODO: 현재는 하드코딩된 memberId를 사용하고 있습니다. 추후 인증 시스템이 구현되면 수정 필요.
        MemberResponseDTO.MyPageResponseDTO myPage = memberQueryService.getMyPage(1L);
        return ApiResponse.onSuccess(myPage);
    }
}

