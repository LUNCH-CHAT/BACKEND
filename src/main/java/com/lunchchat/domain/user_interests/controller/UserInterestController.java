package com.lunchchat.domain.user_interests.controller;

import com.lunchchat.domain.user_interests.converter.UserInterestConverter;
import com.lunchchat.domain.user_interests.dto.UserInterestResponseDTO;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_interests.service.UserInterestQueryService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class UserInterestController {

  private final UserInterestQueryService userInterestQueryService;

  @GetMapping
  @Operation(summary = "관심사 태그 목록 조회", description = "사용 가능한 관심사 목록을 조회합니다.")
  public ApiResponse<UserInterestResponseDTO.UserInterestListDTO> getInterests() {
    List<Interest> interests = userInterestQueryService.getInterests();
    return ApiResponse.onSuccess(UserInterestConverter.toUserInterestPreviewDTO(interests));
  }
}