package com.lunchchat.global.security.auth.controller;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.entity.enums.MemberStatus;
import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.security.auth.dto.GoogleUserDTO;
import com.lunchchat.global.security.auth.service.GoogleAuthService;
import com.lunchchat.global.config.security.JwtConfig;
import com.lunchchat.global.security.jwt.JwtTokenProvider;
import com.lunchchat.global.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/auth")
public class OAuthController {

  private final JwtConfig jwtConfig;
  private final JwtUtil jwtUtil;
  private final JwtTokenProvider jwtTokenProvider;
  private final GoogleAuthService googleAuthService;

  public OAuthController(JwtConfig jwtConfig, JwtUtil jwtUtil, JwtTokenProvider jwtTokenProvider, GoogleAuthService googleAuthService) {
    this.jwtConfig = jwtConfig;
    this.jwtUtil = jwtUtil;
    this.jwtTokenProvider = jwtTokenProvider;
    this.googleAuthService = googleAuthService;
  }

//  // 콜백
//  @GetMapping("/google/callback")
//  public ResponseEntity<Void> googleCallback(@RequestParam("code") String code) {
//    HttpHeaders headers = new HttpHeaders();
//
//    String redirectUri = "프론트 리다이렉트 주소" + code;
//
//    headers.setLocation(URI.create(redirectUri));
//    return new ResponseEntity<>(headers, HttpStatus.FOUND);
//  }



  @GetMapping("/login")
  public ApiResponse<?> googleLogin(@RequestParam("code") String accessCode, HttpServletResponse response) {
    try {
      // 1. 로그인 처리
      GoogleUserDTO.Request request = new GoogleUserDTO.Request(accessCode);
      Member user = googleAuthService.googleAuthLogin(request, response);
      String email = user.getEmail();

      // 2. Access + Refresh Token 발급
      String accessToken = jwtTokenProvider.generateAccessToken(email);
      String refreshToken = jwtTokenProvider.generateRefreshToken(email);

      // 3. 응답 헤더에 담기
      response.setHeader("access", accessToken);
      response.setHeader("refresh", refreshToken);

      // 4. 상태별 응답
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


  // 로그아웃

}
