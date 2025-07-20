package com.lunchchat.global.security.jwt;

import com.lunchchat.global.config.security.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {

  private final JwtParser jwtParser;
  private final SecretKey secretKey;

  public JwtUtil(JwtConfig jwtConfig) {
    this.secretKey = Keys.hmacShaKeyFor(
        Base64.getDecoder().decode(jwtConfig.getSecret()));
    this.jwtParser = Jwts.parser()
        .verifyWith((SecretKey) secretKey)
        .build();
  }

  // 유효성 검사
  public boolean validateToken(String token) {
    if (token == null || token.trim().isEmpty()) return false;
    try {
      jwtParser.parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("JWT validation 오류: {}", e.getMessage());
      return false;
    }
  }

  // 파싱
  public Claims parseJwt(String token) {
    try {
      return jwtParser.parseSignedClaims(token).getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      log.error("claims 파싱 오류: {}", e.getMessage());
      throw new IllegalArgumentException("Invalid JWT token", e);
    }
  }

  public String getEmail(Claims claims) {
    return claims.getSubject();
  }

  public String getType(Claims claims) {
    return claims.get("type", String.class);
  }

  public Date getExpiration(Claims claims) {
    return claims.getExpiration();
  }
}
