package com.lunchchat.global.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtill;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtConfig jwtConfig;

  private final MemberRepository memberRepository;

  // Authentication Manager Bean 등록 -> UsernamePasswordAuthenticationFilter에서 필요
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtConfig jwtConfig) throws Exception {

    //csrf disable
    http
        .csrf((auth) -> auth.disable());

    //cors 설정 (origin 명시적 허용)
    http
        .cors(
            cors -> cors.configurationSource(corsConfigurationSource())
        );

    //기본 Form 로그인 방식 disable -> Custom 인증 로직 사용
    http
        .formLogin((auth) -> auth.disable());

    //http basic 인증 방식 disable (JWT 사용 필수)
    http
        .httpBasic((auth) -> auth.disable());

    //경로별 인가 작업
    http
        .authorizeHttpRequests((auth)-> auth
            .requestMatchers("/swagger", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll() // Swagger 허용
            .requestMatchers("/connect/**").permitAll()
            .requestMatchers("/auth/login/kakao").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
        );

    //JwtFilter
    // 로그인 필터 이전에 동작
    // 토큰 존재 시 -> 토큰 검증 -> 유저 정보 추출 -> SecurityContext에 인증 객체 설정
    http
        .addFilterBefore(new JwtFilter(jwtUtill,jwtConfig,memberRepository), LoginFilter.class);

    //LoginFilter -> UsernamePasswordAuthenticationFilter
    //로그인 요청 가로채 인증시도 -> 성공 시 JWT 발급
    http
        .addFilterBefore((new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtill, jwtConfig,memberRepository)), UsernamePasswordAuthenticationFilter.class);

    // 세션 설정
    // JWT -> Session Stateless (인증상태 토큰으로 확인)
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

    return http.build();
  }

  //cors 요청 허용
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    //허용 오리진 목록
    configuration.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:8080"));

    configuration.addAllowedHeader("*");
    configuration.setExposedHeaders(List.of("access", "Authorization","isNewUser"));

    configuration.setAllowedMethods(List.of("GET","PUT","POST","DELETE"));
    //자격증명 허용
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

