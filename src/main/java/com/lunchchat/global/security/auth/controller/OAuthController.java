package com.lunchchat.global.security.auth.controller;

import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.security.auth.service.GoogleAuthService;
import com.lunchchat.global.config.security.JwtConfig;
import com.lunchchat.global.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class OAuthController {

  private final JwtConfig jwtConfig;
  private final JwtUtil jwtUtil;
  private final GoogleAuthService googleAuthService;

  public OAuthController(JwtConfig jwtConfig, JwtUtil jwtUtil) {
    this.jwtConfig = jwtConfig;
    this.jwtUtil = jwtUtil;
    this.googleAuthService = new GoogleAuthService();
  }


  // 로그인
  @GetMapping("/login/google")
  public ApiResponse<?> login(@RequestParam("code") String accessCode, HttpServletResponse response) {
    try {
      return ApiResponse.onSuccess("LoginSuccess");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // 추가 로그인


  // 로그아웃

}
