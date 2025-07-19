package com.lunchchat.global.security.jwt;

import com.lunchchat.global.config.security.JwtConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final SecretKey secretKey;
  private final JwtConfig jwtConfig;

  public JwtTokenProvider(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
    this.secretKey = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(jwtConfig.getSecret())
    );
  }

  public String generateAccessToken(String email) {
    return createToken("access", email, jwtConfig.getAccessTokenValidityInSeconds());
  }

  public String generateRefreshToken(String email) {
    return createToken("refresh", email, jwtConfig.getRefreshTokenValidityInSeconds());
  }

  private String createToken(String type, String email, Long expiredSeconds) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        //이메일이 sub 주 키로 작용
        .subject(email)
        .claim("type", type)
        .claim("email", email)
        .issuedAt(new Date(now))
        .expiration(new Date(now + expiredSeconds * 1000))
        .signWith(secretKey)
        .compact();
  }
}