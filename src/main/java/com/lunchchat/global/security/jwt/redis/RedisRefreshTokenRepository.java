package com.lunchchat.global.security.jwt.redis;

import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

  private final RedisTemplate<String, Object> redisTemplate;

  //Refresh:<email> -> Token
  private static final String PREFIX = "Refresh:";
  //RefreshIDX:<token> -> email
  private static final String IDX = "RefreshIdx:";

  public RedisRefreshTokenRepository(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  //RT Redis 저장
  @Override
  public void save(String key, String token, Duration ttl) {
    redisTemplate.opsForValue().set(PREFIX + key, token, ttl);
    redisTemplate.opsForValue().set(IDX + token, key, ttl);
  }

  //RT Redis에서 조회
  @Override
  public Optional<String> findByKey(String key) {
    Object value = redisTemplate.opsForValue().get(PREFIX + key);
    return Optional.ofNullable(value).map(Object::toString);
  }

  //RT Request와 Redis 비교
  @Override
  public boolean isValid(String key, String token) {
    return findByKey(key).map(saved -> saved.equals(token)).orElse(false);
  }

  //RT 삭제(email 기반)
  @Override
  public void delete(String key) {
    Object val = redisTemplate.opsForValue().get(PREFIX + key);
    String token = (val != null) ? val.toString() : null;

    redisTemplate.delete(PREFIX + key);
    if (token != null && !token.isBlank()) {
      redisTemplate.delete(IDX + token);
    }
  }

  //RT 삭제(token 기반)
  @Override
  public void deleteByToken(String token) {
    Object key = redisTemplate.opsForValue().get(IDX + token);
    if (key != null) redisTemplate.delete(PREFIX + key);
    redisTemplate.delete(IDX + token);
  }

  //RT rotation
  @Override
  public void rotate(String key, String oldToken, String newToken, Duration ttl){
    if(oldToken!=null && !oldToken.isBlank()){
      redisTemplate.delete(IDX+oldToken);
    }
    redisTemplate.opsForValue().set(PREFIX + key, newToken, ttl);
    redisTemplate.opsForValue().set(IDX + newToken, key, ttl);
    }

}
