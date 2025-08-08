package com.lunchchat.domain.match.controller;

import com.lunchchat.domain.match.converter.MatchConverter;
import com.lunchchat.domain.match.dto.MatchRequestDto;
import com.lunchchat.domain.match.dto.MatchResponseDto;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.match.service.MatchCommandService;
import com.lunchchat.domain.match.service.MatchQueryService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.PaginatedResponse;
import com.lunchchat.global.apiPayLoad.code.status.SuccessStatus;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches")
public class MatchRestController {

  private final MatchCommandService matchCommandService;
  private final MatchQueryService matchQueryService;

  @GetMapping
  @Operation(summary = "매치 목록 조회", description = "사용자의 매치 목록을 상태에 따라 조회합니다.")
  public ApiResponse<PaginatedResponse<MatchResponseDto.MatchListDto>> getMatchList(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @RequestParam(name = "status") MatchStatusType status,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size) {

    String email = customUserDetails.getUsername();
    return ApiResponse.onSuccess(matchQueryService.getMatchListDtosByStatus(status, email, page, size));
  }

  @PostMapping
  @Operation(summary = "매치 요청", description = "매치 요청을 생성합니다. 알림이 자동으로 전송됩니다.")
  public ApiResponse<MatchResponseDto.MatchResultDto> createMatchRequest(
          @AuthenticationPrincipal CustomUserDetails customUserDetails,
          @RequestBody MatchRequestDto.CreateMatchRequest request) {
    
    Matches match = matchCommandService.requestMatch(customUserDetails.getUsername(), request.getToMemberId());
    
    return ApiResponse.onSuccess(MatchConverter.toMatchResultDto(match));
  }

  @PatchMapping("/accept/{otherMemberId}")
  @Operation(summary = "매칭 수락", description = "특정 상대 멤버의 매칭 요청을 수락합니다. 수락 알림이 자동으로 전송됩니다.")
  public ApiResponse<SuccessStatus> acceptMatch(
          @AuthenticationPrincipal CustomUserDetails customUserDetails,
          @Parameter(description = "상대 멤버 ID", required = true) @PathVariable Long otherMemberId) {

    matchCommandService.acceptMatch(otherMemberId, customUserDetails.getUsername());
    
    return ApiResponse.onSuccess(SuccessStatus.MATCH_REQUEST_SUCCESS);
  }
}
