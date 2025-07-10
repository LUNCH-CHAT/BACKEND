package com.lunchchat.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtFilterConfig {

  private final JwtUtil jwtUtill;
  private final JwtConfig jwtConfig;
  private final MemberRepository memberRepository;
  private final AuthenticationManager authenticationManager;

  public void configureJwtFilters(HttpSecurity http) throws Exception {
    // JwtFilter - 토큰 검증 및 인증 객체 설정
    http.addFilterBefore(
        new JwtFilter(jwtUtill, jwtConfig, memberRepository),
        LoginFilter.class
    );

    // LoginFilter - 로그인 요청 처리 및 JWT 발급
    http.addFilterBefore(
        new LoginFilter(authenticationManager, jwtUtill, jwtConfig, memberRepository),
        UsernamePasswordAuthenticationFilter.class
    );
  }

}
