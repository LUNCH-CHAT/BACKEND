package com.lunchchat.global.config.security;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.InputStream;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class GoogleSheetsConfig {

  @Bean
  public Sheets sheetsService() throws Exception {

    // 인증파일
    InputStream input = new ClassPathResource("sheets-key.json").getInputStream();

    // 인증객체 생성
    GoogleCredentials credentials = GoogleCredentials.fromStream(input)
        .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets"));

    // 클라이언트 빌더 생성
    return new Sheets.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JacksonFactory.getDefaultInstance(),
        new HttpCredentialsAdapter(credentials)
    ).setApplicationName("런치챗_이달의멘토").build();
  }
}
