package com.lunchchat.global.config.security;

import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.security.jwt.JwtFilter;
import com.lunchchat.global.security.jwt.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class JwtFilterConfig {

  private final JwtUtil jwtUtil;
  private final JwtConfig jwtConfig;
  private final MemberRepository memberRepository;

  public JwtFilterConfig(JwtUtil jwtUtil, JwtConfig jwtConfig, MemberRepository memberRepository) {
    this.jwtUtil = jwtUtil;
    this.jwtConfig = jwtConfig;
    this.memberRepository = memberRepository;
  }

  public void configureJwtFilters(HttpSecurity http) throws Exception {
    // JwtFilter - 토큰 검증 및 인증 객체 설정
    http.addFilterBefore(
        new JwtFilter(jwtUtil, jwtConfig, memberRepository), UsernamePasswordAuthenticationFilter.class);

//    // LoginFilter - 로그인 요청 처리 및 JWT 발급
//    http.addFilterBefore(
//        new LoginFilter(authenticationManager, jwtUtil, jwtConfig, memberRepository),
//        UsernamePasswordAuthenticationFilter.class
//    );
  }

}
