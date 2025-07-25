package com.lunchchat.global.security.jwt.redis;

import java.time.Duration;
import java.util.Optional;

public interface RefreshTokenRepository {

  void save(String key, String token, Duration ttl);
  Optional<String> findByKey(String key);
  void delete(String key);
  boolean isValid(String key, String token);
  void rotate(String key,String newToken, Duration ttl);

}
