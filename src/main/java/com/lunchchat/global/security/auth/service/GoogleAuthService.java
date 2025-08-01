package com.lunchchat.global.security.auth.service;

import com.lunchchat.domain.college.entity.College;
import com.lunchchat.domain.college.repository.CollegeRepository;
import com.lunchchat.domain.department.entity.Department;
import com.lunchchat.domain.department.repository.DepartmentRepository;
import com.lunchchat.domain.member.entity.Member;
import com.lunchchat.domain.member.entity.enums.LoginType;
import com.lunchchat.domain.member.entity.enums.MemberStatus;
import com.lunchchat.domain.member.repository.MemberRepository;
import com.lunchchat.domain.time_table.entity.TimeTable;
import com.lunchchat.domain.university.entity.University;
import com.lunchchat.domain.university.repository.UniversityRepository;
import com.lunchchat.domain.user_interests.entity.Interest;
import com.lunchchat.domain.user_interests.repository.InterestRepository;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import com.lunchchat.global.apiPayLoad.exception.AuthException;
import com.lunchchat.global.security.auth.dto.GoogleUserDTO;
import com.lunchchat.global.security.auth.dto.TokenDTO;
import com.lunchchat.global.security.auth.dto.TokenDTO.Response;
import com.lunchchat.global.security.auth.infra.CookieUtil;
import com.lunchchat.global.security.auth.infra.GoogleUtil;
import com.lunchchat.global.security.jwt.JwtTokenProvider;
import com.lunchchat.global.security.jwt.JwtUtil;
import com.lunchchat.global.security.jwt.redis.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class GoogleAuthService {

  private final MemberRepository memberRepository;
  private final UniversityRepository universityRepository;
  private final CollegeRepository collegeRepository;
  private final DepartmentRepository departmentRepository;
  private final InterestRepository interestRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final GoogleUtil googleUtil;
  private final JwtUtil jwtUtil;

  public GoogleAuthService(MemberRepository memberRepository,JwtTokenProvider jwtTokenProvider, GoogleUtil googleUtil, UniversityRepository universityRepository, RefreshTokenRepository refreshTokenRepository,
      JwtUtil jwtUtil,CollegeRepository collegeRepository, DepartmentRepository departmentRepository, InterestRepository interestRepository) {
    this.memberRepository = memberRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.googleUtil = googleUtil;
    this.universityRepository = universityRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtUtil = jwtUtil;
    this.collegeRepository = collegeRepository;
    this.departmentRepository = departmentRepository;
    this.interestRepository = interestRepository;
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

  // 회원 생성 메서드
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

  // Reissue
  public Response reissueAccessToken(String refreshToken, HttpServletResponse response) {

    // 1. 리프레시 토큰 검사
    if (refreshToken == null || refreshToken.trim().isEmpty()) {
      throw new AuthException(ErrorStatus.REFRESH_TOKEN_MISSING);
    }

    // 2. 유효성 검사
    if (!jwtUtil.validateToken(refreshToken)) {
      throw new AuthException(ErrorStatus.INVALID_REFRESH_TOKEN);
    }

    // 3. 파싱 후 이메일 추출
    Claims claims = jwtUtil.parseJwt(refreshToken);
    String email = jwtUtil.getEmail(claims);

    // 4. Redis RT와 비교
    if (!refreshTokenRepository.isValid(email, refreshToken)) {
      refreshTokenRepository.delete(email);
      throw new AuthException(ErrorStatus.REUSED_REFRESH_TOKEN);
    }

    log.info("✅Rotate 이전 RT 값 : {}", refreshToken);
    // 5. 토큰 생성
    String newAccessToken = jwtTokenProvider.generateAccessToken(email);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

    // 6. 토큰 rotate
    refreshTokenRepository.rotate(email, newRefreshToken, Duration.ofDays(30));

    // 7. RT 전송
    ResponseCookie refreshCookie = CookieUtil.createCookie(newRefreshToken, Duration.ofDays(30));
    response.setHeader("Set-Cookie", refreshCookie.toString());
    log.info("🚨Rotate 이후 RT 값 : {}", newRefreshToken);

    return new TokenDTO.Response(newAccessToken, newRefreshToken);
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

  //회원가입
  @Transactional
  public void signup(String email, GoogleUserDTO.SingUpRequest dto) {

    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new AuthException(ErrorStatus.USER_NOT_FOUND));

    if (memberRepository.existsByStudentNo(dto.studentNo())) {
      throw new AuthException(ErrorStatus.DUPLICATE_STUDENTNO);
    }

    if (dto.interests().size() > 3) {
      throw new AuthException(ErrorStatus.INTEREST_MAX_THREE);
    }

    // 연관 엔티티 조회
    College college = collegeRepository.findById(dto.collegeId())
        .orElseThrow(() -> new AuthException(ErrorStatus.COLLEGE_NOT_FOUND));

    Department department = departmentRepository.findById(dto.departmentId())
        .orElseThrow(() -> new AuthException(ErrorStatus.DEPARTMENT_NOT_FOUND));

    Set<Interest> interests = dto.interests().stream()
        .map(type -> interestRepository.findByType(type)
            .orElseThrow(() -> new AuthException(ErrorStatus.INTEREST_NOT_FOUND)))
        .collect(Collectors.toSet());

    // member 업데이트
    member.signUp(
        dto.membername(),
        dto.studentNo(),
        college,
        department,
        interests
    );

    // 시간표 처리
    List<TimeTable> timeTables = dto.timeTables().stream()
        .map(ttDto -> TimeTable.create(
            ttDto.dayOfWeek(),
            ttDto.startTime(),
            ttDto.endTime(),
            ttDto.subjectName()
        ))
        .collect(Collectors.toList());

    member.addTimeTables(timeTables);

    memberRepository.save(member);
  }

}
