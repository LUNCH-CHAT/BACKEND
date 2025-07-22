package com.lunchchat.global.security.auth.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.entity.enums.LoginType;
import com.lunchchat.domain.member.entity.enums.MemberStatus;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.university.entity.University;
import com.lunchchat.domain.university.repository.UniversityRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.AuthException;
import com.lunchchat.global.security.auth.dto.GoogleUserDTO;
import com.lunchchat.global.security.auth.infra.CookieUtil;
import com.lunchchat.global.security.auth.infra.GoogleUtil;
import com.lunchchat.global.security.jwt.JwtTokenProvider;
import com.lunchchat.global.security.jwt.redis.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleAuthService {

  private final MemberRepository memberRepository;
  private final UniversityRepository universityRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final GoogleUtil googleUtil;

  public GoogleAuthService(MemberRepository memberRepository,JwtTokenProvider jwtTokenProvider, GoogleUtil googleUtil, UniversityRepository universityRepository, RefreshTokenRepository refreshTokenRepository) {
    this.memberRepository = memberRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.googleUtil = googleUtil;
    this.universityRepository = universityRepository;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  //구글 로그인
  public Member googleAuthLogin(GoogleUserDTO.Request request, HttpServletResponse response) {
    // 1. 구글 토큰 요청
    GoogleUserDTO.TokenResponse token = googleUtil.requestToken(request.code());

    // 2. 사용자 정보 요청
    GoogleUserDTO.ProfileResponse profile = googleUtil.requestProfile(token);

    String email = profile.email();
    String name = profile.name();

    // 3. 기존 회원 조회 or 신규 생성
    Member user = memberRepository.findByEmail(email)
        .orElseGet(() -> createNewUser(email, name));

    // 4. 탈퇴 회원 차단
    if (user.getStatus() == MemberStatus.INACTIVE) {
      throw new AuthException(ErrorStatus.ACCOUNT_DISABLED);
    }

    // 5. JWT 발급 (Access + RefreshToken)
    String accessToken = jwtTokenProvider.generateAccessToken(email);
    String refreshToken = jwtTokenProvider.generateRefreshToken(email);

    // 6. RefreshToken Redis 저장
    refreshTokenRepository.save(email, refreshToken, Duration.ofDays(30));

    // 7. RT 쿠키 설정
    ResponseCookie refreshCookie = ResponseCookie.from("refresh", refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(Duration.ofDays(14))
        .sameSite("Strict")
        .build();

    // 8. 응답 헤더 & 쿠키 세팅
    response.setHeader("access", accessToken);
    response.setHeader("Set-Cookie", CookieUtil.createCookie(refreshToken,Duration.ofDays(30)).toString());


    log.info("✅ [구글 로그인] email={}, name={}, access={}", email, name, accessToken);
    log.info("✅ [Redis에 저장된 RT] RT={}", refreshToken);

    // 9. 신규 가입 여부 전달
    boolean isNewUser = user.getStatus()==MemberStatus.PENDING;
    response.setHeader("isNewUser", String.valueOf(isNewUser));

    return user;
  }

  private Member createNewUser(String email, String name) {
    log.info("🆕 신규 구글 회원 등록: email={}, name={}", email, name);

    String domain = extractDomainFromEmail(email);
    University university = universityRepository.findByDomain(domain)
        .orElseGet(this::getFallbackUniversity);

    Member newUser = new Member(
        email,
        name,
        LoginType.Google,
        MemberStatus.PENDING,
        "NO_PASSWORD",
        "ROLE_USER"
    );
    newUser.setUniversity(university);

    return memberRepository.save(newUser);
  }

  //학교 도메인 별 이메일 분류
  private String extractDomainFromEmail(String email) {
    if (email == null || !email.contains("@")) {
      throw new AuthException(ErrorStatus.INVALID_EMAIL_FORMAT);
    }
    return email.substring(email.indexOf("@") + 1).toLowerCase(); // e.g., "ewhain.net"
  }

  //기본 대학 = UMC대
  private University getFallbackUniversity() {
    return universityRepository.findByName("UMC대")
        .orElseThrow(() -> new IllegalStateException("기본 대학 'UMC대'가 DB에 존재하지 않습니다."));
  }

}
