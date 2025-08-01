package com.lunchchat.global.security.auth.infra;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
@Setter
@Getter
public class GoogleOAuthProperties {

  private String clientId;
  private String clientSecret;
  private String redirectUri;

}
