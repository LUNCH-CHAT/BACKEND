package com.lunchchat.global.security.jwt;

import com.lunchchat.global.config.security.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  private final SecretKey secretKey;

  public JwtUtil(JwtConfig jwtConfig) {
    this.secretKey = new SecretKeySpec(
        jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8),
        Jwts.SIG.HS256.key().build().getAlgorithm()
    );
  }

}
