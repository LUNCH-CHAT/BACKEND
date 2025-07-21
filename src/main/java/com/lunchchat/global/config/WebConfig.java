package com.lunchchat.global.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();

    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(new FormHttpMessageConverter()); // application/x-www-form-urlencoded
    messageConverters.add(new MappingJackson2HttpMessageConverter()); // JSON 직렬화용

    restTemplate.setMessageConverters(messageConverters);
    return restTemplate;
  }
}
