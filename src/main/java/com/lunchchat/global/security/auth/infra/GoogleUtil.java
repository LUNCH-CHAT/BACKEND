package com.lunchchat.global.security.auth.infra;

import com.lunchchat.global.security.auth.dto.GoogleUserDTO;
import com.lunchchat.global.security.auth.dto.GoogleUserDTO.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GoogleUtil {

  private final RestTemplate restTemplate;
  private final GoogleOAuthProperties props;

  public GoogleUserDTO.TokenResponse requestToken(String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", props.getClientId());
    params.add("client_secret", props.getClientSecret());
    params.add("redirect_uri", props.getRedirectUri());
    params.add("code", code);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

    ResponseEntity<GoogleUserDTO.TokenResponse> response = restTemplate.postForEntity(
        "https://oauth2.googleapis.com/token",
        request,
        GoogleUserDTO.TokenResponse.class
    );

    return response.getBody();
  }

  public GoogleUserDTO.ProfileResponse requestProfile(GoogleUserDTO.TokenResponse token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token.accessToken());

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<GoogleUserDTO.ProfileResponse> response = restTemplate.exchange(
        "https://www.googleapis.com/oauth2/v2/userinfo",
        HttpMethod.GET,
        request,
        GoogleUserDTO.ProfileResponse.class
    );

    return response.getBody();
  }

}
