package com.lunchchat.global.security.jwt.redis;

import java.time.Duration;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

  private final RedisTemplate<String, Object> redisTemplate;
  private static final String PREFIX = "Refresh:";
  private static final String IDX = "RefreshIdx:";

  public RedisRefreshTokenRepository(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  //RT Redis 저장
  @Override
  public void save(String key, String token, Duration ttl) {
    redisTemplate.opsForValue().set(PREFIX + key, token, ttl);
    //역인덱스
    redisTemplate.opsForValue().set(IDX+token,key,ttl);
  }

  //RT Redis에서 조회
  @Override
  public Optional<String> findByKey(String key) {
    Object value = redisTemplate.opsForValue().get(PREFIX + key);
    return Optional.ofNullable(value).map(Object::toString);
  }

  //RT Redis에서 조회
  @Override
  public void delete(String key) {
    String token = (String) redisTemplate.opsForValue().get(PREFIX + key);
    redisTemplate.delete(PREFIX + key);
    if (token != null) redisTemplate.delete(IDX + token);
  }

  //RT Redis에서 삭제
  @Override
  public void deleteByToken(String token) {
    Object key = redisTemplate.opsForValue().get(IDX + token);
    if (key != null) redisTemplate.delete(PREFIX + key);
    redisTemplate.delete(IDX + token);
  }

  //RT Request와 Redis 비교
  @Override
  public boolean isValid(String key, String token) {
    return findByKey(key).map(saved -> saved.equals(token)).orElse(false);
  }

  //RT rotation
  @Override
  public void rotate(String key, String newToken, Duration ttl){
    save(key, newToken, ttl);
  }

}
