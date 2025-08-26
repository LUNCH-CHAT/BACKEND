package com.lunchchat.global.security.auth.service;

import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.entity.enums.LoginType;
import com.lunchchat.domain.member.entity.enums.MemberStatus;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.university.entity.University;
import com.lunchchat.domain.university.repository.UniversityRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.AuthException;
import com.lunchchat.global.security.auth.dto.DirectLoginDTO;
import com.lunchchat.global.security.auth.infra.CookieUtil;
import com.lunchchat.global.security.jwt.JwtTokenProvider;
import com.lunchchat.global.security.jwt.redis.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Slf4j
public class DirectAuthService {

    private final MemberRepository memberRepository;
    private final UniversityRepository universityRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public DirectAuthService(
            MemberRepository memberRepository,
            UniversityRepository universityRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder
    ) {
        this.memberRepository = memberRepository;
        this.universityRepository = universityRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public DirectLoginDTO.Response directLogin(DirectLoginDTO.Request request, HttpServletResponse response) {
        String email = request.email();
        String password = request.password();

        // 1. 사용자 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ErrorStatus.USER_NOT_FOUND));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(ErrorStatus.UNAUTHORIZED);
        }

        // 3. 계정 상태 확인
        if (member.getStatus() == MemberStatus.INACTIVE) {
            throw new AuthException(ErrorStatus.ACCOUNT_DISABLED);
        }

        // 4. JWT 토큰 발급
        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        // 5. RefreshToken Redis 저장
        refreshTokenRepository.save(email, refreshToken, Duration.ofDays(30));

        // 6. RefreshToken 쿠키 설정
        ResponseCookie refreshCookie = CookieUtil.createCookie(refreshToken, Duration.ofDays(30));
        response.setHeader("Set-Cookie", refreshCookie.toString());

        // 7. AccessToken 헤더 설정
        response.setHeader("access", accessToken);

        // 8. 신규 회원 여부 확인
        boolean isNewUser = member.getStatus() == MemberStatus.PENDING;
        response.setHeader("isNewUser", String.valueOf(isNewUser));

        log.info("✅ [직접 로그인 성공] email={}, membername={}", email, member.getMembername());

        return new DirectLoginDTO.Response(
                accessToken,
                refreshToken,
                email,
                member.getMembername(),
                isNewUser
        );
    }

    @Transactional
    public DirectLoginDTO.Response registerAndLogin(DirectLoginDTO.Request request, HttpServletResponse response) {
        String email = request.email();
        String password = request.password();

        // 1. 이미 존재하는 사용자인지 확인
        if (memberRepository.existsByEmail(email)) {
            throw new AuthException(ErrorStatus.EMAIL_EXISTS);
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 3. 이메일에서 도메인 추출하여 대학 찾기
        String domain = extractDomainFromEmail(email);
        University university = universityRepository.findByDomain(domain)
                .orElseGet(this::getFallbackUniversity);

        // 4. 새 사용자 생성
        String name = email.substring(0, email.indexOf("@")); // 이메일 앞부분을 기본 이름으로 사용
        Member newMember = new Member(
                email,
                name,
                LoginType.Direct,
                MemberStatus.PENDING,
                encodedPassword,
                "ROLE_USER"
        );
        newMember.setUniversity(university);

        // 5. 사용자 저장
        memberRepository.save(newMember);

        // 6. 로그인 처리
        return directLogin(request, response);
    }

    private String extractDomainFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new AuthException(ErrorStatus.INVALID_EMAIL_FORMAT);
        }
        return email.substring(email.indexOf("@") + 1).toLowerCase();
    }

    private University getFallbackUniversity() {
        return universityRepository.findByName("UMC대")
                .orElseThrow(() -> new IllegalStateException("기본 대학 'UMC대'가 DB에 존재하지 않습니다."));
    }
}