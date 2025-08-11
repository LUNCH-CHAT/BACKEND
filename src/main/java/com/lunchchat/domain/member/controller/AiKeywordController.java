package com.lunchchat.domain.member.controller;

import com.lunchchat.domain.member.dto.KeywordRecommendationDTO;
import com.lunchchat.domain.member.service.AiKeywordService;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class AiKeywordController {

   private final AiKeywordService aiKeywordService;

   public AiKeywordController(AiKeywordService aiKeywordService){
     this.aiKeywordService=aiKeywordService;
   }

  @PostMapping("/keywordAI")
  public ApiResponse<KeywordRecommendationDTO.response> generateKeyword(
      @RequestBody @Valid KeywordRecommendationDTO.request request) {

    String keyword = aiKeywordService.generateKeyword(request.description());
    return ApiResponse.onSuccess(new KeywordRecommendationDTO.response(keyword));
  }


}
