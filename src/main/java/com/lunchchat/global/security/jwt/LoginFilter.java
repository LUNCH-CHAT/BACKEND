package com.lunchchat.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.config.security.JwtConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final JwtConfig jwtConfig;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final MemberRepository memberRepository;

  public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, JwtConfig jwtConfig, MemberRepository memberRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.jwtConfig = jwtConfig;
    this.memberRepository = memberRepository;
  }


  // 로그인 시도 메서드 -> UsernamePasswordAuthenticationFilter에서 상속
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{

    try{

      return authenticationManager.authenticate();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  // 로그인 성공시 메서드
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

    response.setStatus(HttpStatus.OK.value());
  }

  // 로그인 실패 메서드
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    response.setStatus(401);
  }


}
