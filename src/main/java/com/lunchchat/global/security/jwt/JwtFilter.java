package com.lunchchat.global.security.jwt;

import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.exception.handler.MemberHandler;
import com.lunchchat.global.config.security.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final JwtConfig jwtConfig;
  private final MemberRepository memberRepository;

  public JwtFilter(JwtUtil jwtUtil, JwtConfig jwtConfig, MemberRepository memberRepository) {
    this.jwtUtil = jwtUtil;
    this.jwtConfig = jwtConfig;
    this.memberRepository = memberRepository;
  }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


    }


}

