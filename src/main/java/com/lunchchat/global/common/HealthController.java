package com.lunchchat.global.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@RestController
@RequestMapping("/health")
@Tag(name = "서버 안정성 체크 API")
public class HealthController {

  @GetMapping
  @Operation(summary = "헬스 체크 API")
  public Map<String, Object> health() {
    Map<String, Object> result = new HashMap<>();
    result.put("status", "healthy!");
    result.put("systemTimeZone", TimeZone.getDefault().getID());
    result.put("localDateTime", LocalDateTime.now());
    result.put("koreaTime", ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
    result.put("utcTime", ZonedDateTime.now(ZoneId.of("UTC")));
    result.put("jvmTimeZone", System.getProperty("user.timezone"));
    return result;
  }
}
