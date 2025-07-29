package com.lunchchat.global.security.auth.controller;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.entity.enums.MemberStatus;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.university.entity.University;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import com.lunchchat.global.security.auth.dto.GoogleUserDTO;
import com.lunchchat.global.security.auth.dto.TokenDTO;
import com.lunchchat.global.security.auth.service.GoogleAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/auth")
public class OAuthController {

  private final GoogleAuthService googleAuthService;
  private final MemberRepository memberRepository;

  public OAuthController(GoogleAuthService googleAuthService, MemberRepository memberRepository) {
    this.googleAuthService = googleAuthService;
    this.memberRepository = memberRepository;
  }

  @GetMapping("/callback/google")
  public void redirectTo(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
    String redirectUri = "http://localhost:5173/auth/callback/google?code=" + code;
    response.sendRedirect(redirectUri);;
  }


  @GetMapping("/login/google")
  public ApiResponse<?> googleLogin(@RequestParam("code") String accessCode, HttpServletResponse response) {
    try {
      Member user = googleAuthService.googleAuthLogin(new GoogleUserDTO.Request(accessCode), response);

      // 상태별 응답
      if (user.getStatus() == MemberStatus.PENDING) {
        return ApiResponse.onSuccess("isNewUser");
      } else {
        return ApiResponse.onSuccess("로그인 성공");
      }

    } catch (Exception e) {
      log.error("❌ [로그인 실패] code = {}, error = {}", accessCode, e.getMessage());
      return ApiResponse.error(ErrorStatus.UNAUTHORIZED, "accessToken이 유효하지 않습니다");
    }
  }

  // 추가 로그인
  @PatchMapping("/signUp/lunchChat")
  public ApiResponse<?> Signup (@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody @Valid GoogleUserDTO.SingUpRequest dto){
    String email = userDetails.getUsername();
    googleAuthService.signup(email, dto);

    return ApiResponse.onSuccess("추가 회원정보 등록 완료");
  }

  // 로그아웃

  //Reissue
  @Transactional
  @PostMapping("/reissue")
  public ApiResponse<TokenDTO.Response> reissue(@CookieValue(name = "refresh", required = false) String refreshToken, HttpServletResponse response) {
    log.info("🍪 [Reissue 요청] 전달된 refreshToken 쿠키 값: {}", refreshToken);
    TokenDTO.Response tokenResponse = googleAuthService.reissueAccessToken(refreshToken, response);
    return ApiResponse.onSuccess(tokenResponse);
  }

  // 대학 간단 조회
  @GetMapping("/uniName")
  public ResponseEntity<String> getUniversityName(Authentication authentication) {
    String email = authentication.getName();
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

    University university = member.getUniversity();
    if (university == null) {
      return ResponseEntity.ok("대학 정보 없음");
    }
    return ResponseEntity.ok(university.getName());
  }

}