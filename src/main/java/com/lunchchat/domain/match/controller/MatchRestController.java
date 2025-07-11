package com.lunchchat.domain.match.controller;

import com.lunchchat.domain.match.dto.MatchResponseDto;
import com.lunchchat.domain.match.dto.enums.MatchStatusType;
import com.lunchchat.domain.match.service.MatchQueryService;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.handler.MemberHandler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/matches")
public class MatchRestController {

  private final MatchQueryService matchQueryService;
  private final MemberRepository memberRepository;

  @GetMapping
  public ApiResponse<List<MatchResponseDto.MatchListDto>> getMatchList(
      @RequestParam(name = "status") MatchStatusType status) {

    // TODO: 현재는 테스트용으로 하드코딩된 사용자 ID를 사용합니다.
    Member fakeUser = memberRepository.findById(1L)
        .orElseThrow(() -> new MemberHandler(ErrorStatus.USER_NOT_FOUND));

    return ApiResponse.onSuccess(matchQueryService.getMatchListDtosByStatus(status, fakeUser.getId()));
  }
}