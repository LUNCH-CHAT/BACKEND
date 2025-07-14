package com.lunchchat.domain.match.controller;

import com.lunchchat.domain.match.converter.MatchConverter;
import com.lunchchat.domain.match.dto.MatchResponseDto;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.match.entity.Matches;
import com.lunchchat.domain.match.service.MatchCommandService;
import com.lunchchat.domain.match.service.MatchQueryService;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.MemberHandler;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches")
public class MatchRestController {

  private final MatchCommandService matchCommandService;
  private final MatchQueryService matchQueryService;
  private final MemberRepository memberRepository;

  @GetMapping
  @Operation(summary = "매치 목록 조회", description = "사용자의 매치 목록을 상태에 따라 조회합니다. 현재는 테스트용으로 하드코딩된 사용자 ID를 사용합니다.")
  public ApiResponse<List<MatchResponseDto.MatchListDto>> getMatchList(
      @RequestParam(name = "status") MatchStatusType status) {

    // TODO: 현재는 테스트용으로 하드코딩된 사용자 ID를 사용합니다.
    Member fakeUser = memberRepository.findById(1L)
        .orElseThrow(() -> new MemberHandler(ErrorStatus.USER_NOT_FOUND));

    return ApiResponse.onSuccess(matchQueryService.getMatchListDtosByStatus(status, fakeUser.getId()));
  }

  @PostMapping
  @Operation(summary = "매치 요청", description = "매치 요청을 생성합니다.")
  public ApiResponse<MatchResponseDto.MatchResultDto> createMatchRequest(@RequestBody Long toMemberId) {
    // TODO: 인증 유저로 교체
    Matches match = matchCommandService.requestMatch(1L, toMemberId);
    return ApiResponse.onSuccess(MatchConverter.toMatchResultDto(match));
  }

  @PatchMapping("/{id}/accept")
  @Operation(summary = "매칭 수락", description = "특정 매칭 요청을 수락합니다.")
  public ApiResponse<Void> acceptMatch(@PathVariable Long id) {

    // TODO: 인증 유저로 교체
    Long currentUserId = 1L;

    matchCommandService.acceptMatch(id, currentUserId);
    return ApiResponse.onSuccess(null);
  }
}