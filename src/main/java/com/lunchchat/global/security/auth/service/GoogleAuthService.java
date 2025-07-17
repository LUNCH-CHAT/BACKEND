package com.lunchchat.global.security.auth.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.entity.enums.LoginType;
import com.lunchchat.domain.member.entity.enums.MemberStatus;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.AuthException;
import com.lunchchat.global.config.security.JwtConfig;
import com.lunchchat.global.security.auth.dto.GoogleUserDTO;
import com.lunchchat.global.security.auth.infra.GoogleUtil;
import com.lunchchat.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class GoogleAuthService {

  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final GoogleUtil googleUtil;

  public GoogleAuthService(MemberRepository memberRepository,JwtTokenProvider jwtTokenProvider, GoogleUtil googleUtil) {
    this.memberRepository = memberRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.googleUtil = googleUtil;
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

    // 5. JWT 발급 및 응답 헤더에 설정
    String accessToken = jwtTokenProvider.generateAccessToken(email);
    response.setHeader("access", accessToken);

    log.info("✅ [구글 로그인] email={}, name={}, access={}", email, name, accessToken);

    boolean isNewUser = user.getStatus()==MemberStatus.PENDING;
    response.setHeader("isNewUser", String.valueOf(isNewUser));

    return user;
  }

  private Member createNewUser(String email, String name) {
    log.info("🆕 신규 구글 회원 등록: email={}, name={}", email, name);

    Member newUser = new Member(
        email,
        name,
        LoginType.Google,
        MemberStatus.PENDING,
        "NO_PASSWORD",
        "ROLE_USER"
    );

    return memberRepository.save(newUser);
  }

}
