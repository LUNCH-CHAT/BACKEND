package com.lunchchat.domain.member.controller;

import com.lunchchat.domain.member.dto.MemberRequestDTO;
import com.lunchchat.domain.member.dto.MemberFilterRequestDTO;
import com.lunchchat.domain.member.dto.MemberResponseDTO;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.member.service.MemberCommandService;
import com.lunchchat.domain.member.service.MemberQueryService;
import com.lunchchat.domain.member.service.ProfileImageService;
import com.lunchchat.domain.notification.dto.FcmUpdateRequestDto;
import com.lunchchat.domain.user_keywords.dto.UserKeywordDTO;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.PaginatedResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.code.status.SuccessStatus;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
  
    private final MemberQueryService memberQueryService;
    private final MemberCommandService memberCommandService;
    private final MemberRepository memberRepository;
    private final ProfileImageService profileImageService;

    @PatchMapping("/fcm-token")
    @Operation(summary = "FCM 토큰 업데이트", description = "사용자의 FCM 토큰을 업데이트합니다.")
    public ApiResponse<SuccessStatus> updateFcmToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FcmUpdateRequestDto request) {
        memberCommandService.updateFcmToken(userDetails.getUsername(), request.getFcmToken());
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
    public ApiResponse<PaginatedResponse<MemberResponseDTO.MemberRecommendationResponseDTO>> filterMembers(
            @Valid @ModelAttribute MemberFilterRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();
        PaginatedResponse<MemberResponseDTO.MemberRecommendationResponseDTO> results =
                memberQueryService.getFilteredRecommendations(email, request);

        return ApiResponse.onSuccess(results);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 멤버 조회", description = "점수 기준으로 인기 멤버를 조회합니다.")
    public ApiResponse<List<MemberResponseDTO.MemberRecommendationResponseDTO>> getPopularMembers(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        Member viewer = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorStatus.USER_NOT_FOUND));
        List<MemberResponseDTO.MemberRecommendationResponseDTO> response = memberQueryService.getPopularMembers(viewer.getId());
        return ApiResponse.onSuccess(response);
    }


    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 조회", description = "마이페이지를 조회합니다.")
    public ApiResponse<MemberResponseDTO.MyPageResponseDTO> getMyPageInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberResponseDTO.MyPageResponseDTO myPage = memberQueryService.getMyPage(userDetails.getUsername());
        return ApiResponse.onSuccess(myPage);
    }

    @PatchMapping("/me/tags")
    @Operation(summary = "사용자 관심사 태그 업데이트", description = "사용자의 관심사 태그를 업데이트합니다.")
    public ApiResponse<SuccessStatus> updateUserInterest(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid MemberRequestDTO.UpdateInterestDTO request) {
        String email = userDetails.getUsername();
        memberCommandService.updateInterests(email, request.getInterestIds());
        return ApiResponse.onSuccess(SuccessStatus.INTERESTS_UPDATE_SUCCESS);
    }

    @PatchMapping("/keywords")
    @Operation(summary = "사용자 키워드 등록", description = "사용자의 키워드를 등록 또는 업데이트합니다.")
    public ApiResponse<SuccessStatus> updateUserKeywords(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid MemberRequestDTO.UpdateKeywordListDTO request) {
        String email = userDetails.getUsername();
        memberCommandService.updateKeywords(email, request);
        return ApiResponse.onSuccess(SuccessStatus.KEYWORDS_UPDATE_SUCCESS);
    }

    @GetMapping("/keywords")
    @Operation(summary = "내 키워드 조회", description = "현재 로그인한 사용자의 키워드를 조회합니다.")
    public ApiResponse<List<UserKeywordDTO>> getMyKeywords(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        List<UserKeywordDTO> keywords = memberQueryService.getUserKeywords(email);
        return ApiResponse.onSuccess(keywords);
    }

    @GetMapping("/me")
    @Operation(summary = "내 프로필 상세 조회", description = "내 프로필을 상세 조회합니다.")
    public ApiResponse<MemberResponseDTO.MyProfileDetailResponseDTO> getMyProfileDetail(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        MemberResponseDTO.MyProfileDetailResponseDTO detail = memberQueryService.getMyDetail(userDetails.getUsername());
        return ApiResponse.onSuccess(detail);
    }

    @PatchMapping("/profile-image")
    @Operation(summary = "프로필 이미지 URL 저장", description = "이미지 업로드 완료 후 S3 URL을 저장합니다.")
    public ApiResponse<SuccessStatus> updateProfileImageUrl(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody @Valid MemberRequestDTO.UpdateProfileImageRequest dto
    ) {
        memberCommandService.updateProfileImageUrl(userDetails.getUsername(), dto.getProfileImageUrl());
        return ApiResponse.onSuccess(SuccessStatus.PROFILE_IMAGE_UPLOAD_SUCCESS);
    }
}
