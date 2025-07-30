package com.lunchchat.domain.match.controller;

import com.lunchchat.domain.match.converter.MatchConverter;
import com.lunchchat.domain.match.dto.MatchRequestDto;
import com.lunchchat.domain.match.dto.MatchResponseDto;
import com.lunchchat.domain.match.dto.MatchResponseDto.MatchListDto;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.match.service.MatchCommandService;
import com.lunchchat.domain.match.service.MatchQueryService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
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
  public ApiResponse<List<MatchListDto>> getMatchList(
      @AuthenticationPrincipal CustomUserDetails customUserDetails,
      @RequestParam(name = "status") MatchStatusType status) {

    String email = customUserDetails.getUsername();
    return ApiResponse.onSuccess(matchQueryService.getMatchListDtosByStatus(status, email));
  }

  @PostMapping
  @Operation(summary = "매치 요청", description = "매치 요청을 생성합니다. 알림이 자동으로 전송됩니다.")
  public ApiResponse<MatchResponseDto.MatchResultDto> createMatchRequest(
          @AuthenticationPrincipal CustomUserDetails customUserDetails,
          @RequestBody MatchRequestDto.CreateMatchRequest request) {
    
    Matches match = matchCommandService.requestMatch(customUserDetails.getUsername(), request.getToMemberId());
    
    return ApiResponse.onSuccess(MatchConverter.toMatchResultDto(match));
  }

  @PatchMapping("/{matchId}/accept")
  @Operation(summary = "매칭 수락", description = "특정 매칭 요청을 수락합니다. 수락 알림이 자동으로 전송됩니다.")
  public ApiResponse<Void> acceptMatch(
          @Parameter(description = "매칭 ID", required = true) @PathVariable Long matchId,
          @Parameter(description = "수락하는 사용자 ID", required = true) @RequestParam Long memberId) {

    matchCommandService.acceptMatch(matchId, memberId);
    
    return ApiResponse.onSuccess(null);
  }
}
