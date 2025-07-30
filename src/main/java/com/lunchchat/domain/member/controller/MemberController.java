package com.lunchchat.domain.member.controller;

import com.lunchchat.domain.member.dto.MemberFilterRequestDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.member.service.MemberCommandService;
import com.lunchchat.domain.member.service.MemberQueryService;
import com.lunchchat.domain.notification.dto.FcmUpdateRequestDto;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.code.status.SuccessStatus;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
  
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final MemberRepository memberRepository;

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
        @PathVariable Long memberId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        String viewerEmail = userDetails.getUsername();
        MemberResponseDTO.MemberDetailResponseDTO detail = memberQueryService.getMemberDetail(memberId, viewerEmail);
        return ApiResponse.onSuccess(detail);
    }

    @GetMapping("/recommendations")
    @Operation(summary = "시간표, 관심사가 겹치는 프로필 추천", description = "시간표 & 관심사 기준 추천 사용자들을 조회합니다.")
    public ApiResponse<List<MemberResponseDTO.MemberRecommendationResponseDTO>> getRecommendedMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        Member viewer = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));
        return ApiResponse.onSuccess(memberQueryService.getRecommendedMembers(viewer.getId()));
    }

    @GetMapping("/filters")
    @Operation(summary = "프로필 필터 조회")
    public ApiResponse<List<MemberResponseDTO.MemberRecommendationResponseDTO>> filterMembers(
            @Valid @ModelAttribute MemberFilterRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();
        List<MemberResponseDTO.MemberRecommendationResponseDTO> results =
                memberQueryService.getFilteredRecommendations(email, request);

        return ApiResponse.onSuccess(results);
    }

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회", description = "마이페이지를 조회합니다.")
    public ApiResponse<MemberResponseDTO.MyPageResponseDTO> getMyPageInfo(Member member) {
        // TODO: 현재는 하드코딩된 memberId를 사용하고 있습니다. 추후 인증 시스템이 구현되면 수정 필요.
        MemberResponseDTO.MyPageResponseDTO myPage = memberQueryService.getMyPage(1L);
        return ApiResponse.onSuccess(myPage);
    }

}

