package com.lunchchat.global.config.security;

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
public class SecurityConfig {

  private final CorsConfigurationSource corsConfigurationSource;
  private final JwtFilterConfig jwtFilterConfig;

  public SecurityConfig(CorsConfigurationSource corsConfigurationSource,JwtFilterConfig jwtFilterConfig) {
    this.corsConfigurationSource = corsConfigurationSource;
    this.jwtFilterConfig = jwtFilterConfig;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    // 보안 정책 설정 (CSRF, 세션, 인가 규칙 등)
    http
        .csrf((auth) -> auth.disable());

    //기본 Form 로그인 방식 disable -> Custom 인증 로직 사용
    http
        .formLogin((auth) -> auth.disable());

    //http basic 인증 방식 disable (JWT 사용 필수)
    http
        .httpBasic((auth) -> auth.disable());

    // 세션 관리 - JWT 사용으로 Stateless
    http.sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    //경로별 인가 작업
    http
        .authorizeHttpRequests((auth)-> auth
            .requestMatchers("/swagger", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll() // Swagger 허용
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
        );


    // CORS 설정
    http.cors(cors -> cors.configurationSource(corsConfigurationSource));

    // JWT 필터 설정
    jwtFilterConfig.configureJwtFilters(http);

    return http.build();
  }

}

