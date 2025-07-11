package com.lunchchat.global.security.jwt;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.entity.enums.MemberStatus;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.AuthException;
import com.lunchchat.global.config.security.JwtConfig;
import com.lunchchat.global.security.auth.dto.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
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

  private static final String AUTH_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    // 1. Authorization 헤더 확인
    String authorization = request.getHeader(AUTH_HEADER);
    if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
      log.debug("Authorization 헤더 누락 또는 형식 오류");
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Bearer 접두사 제거 및 토큰 추출
    String token = authorization.substring(BEARER_PREFIX.length()).trim();

    // 3. JWT 파싱 및 검증
    Claims claims;
    try {
      claims = jwtUtil.parseJwt(token);
    } catch (ExpiredJwtException e) {
      log.warn("AccessToken 만료: {}", e.getMessage());
      throw new AuthException(ErrorStatus.TOKEN_EXPIRED);
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("JWT 유효하지 않음: {}", e.getMessage());
      throw new AuthException(ErrorStatus.JWT_INVALID_TOKEN);
    }

    // 4. 토큰 만료 여부 확인
    if (jwtUtil.getExpiration(claims).before(new Date())) {
      log.warn("AccessToken이 만료되었습니다");
      throw new AuthException(ErrorStatus.TOKEN_EXPIRED);
    }

    // 5. 토큰 타입 확인
    String tokenType = jwtUtil.getType(claims);
    if (!"access".equals(tokenType)) {
      log.warn("토큰 타입이 오류 : {}", tokenType);
      throw new AuthException(ErrorStatus.TOKEN_MALFORMED);
    }

    // 6. 이메일로 회원 조회
    String email = jwtUtil.getEmail(claims);
    Member member = memberRepository.findByEmail(email).orElse(null);
    if (member == null) {
      log.warn("해당 이메일로 등록된 회원 없음: {}", email);
      throw new AuthException(ErrorStatus.USER_NOT_FOUND);
    }

    // 7. 회원 상태 확인
    if (member.getStatus() == MemberStatus.INACTIVE) {
      log.info("INACTIVE 회원 접근: {}", email);
      throw new AuthException(ErrorStatus.ACCOUNT_DISABLED);
    }

    // 8. 인증 객체 생성 및 SecurityContext 등록
    CustomUserDetails userDetails = new CustomUserDetails(member);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 9. 다음 필터로 전달
    filterChain.doFilter(request, response);
  }

}

