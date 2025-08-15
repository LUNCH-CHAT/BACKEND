package com.lunchchat.global.security.auth.infra;

import java.time.Duration;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

  public static ResponseCookie createCookie(String token, Duration ttl) {

    return ResponseCookie.from("refresh", token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(ttl)
        .sameSite("None")
        .build();
  }

  public static ResponseCookie deleteCookie() {
    return ResponseCookie.from("refresh", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0) // 즉시 만료
        .sameSite("None")
        .build();
  }
}
