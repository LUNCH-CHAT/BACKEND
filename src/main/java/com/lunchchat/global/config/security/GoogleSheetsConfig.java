package com.lunchchat.global.config.security;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleSheetsConfig {

    @Value("${GOOGLE_SHEETS_SERVICE_ACCOUNT_FILE}")
    private String serviceAccountFilePath;

    @Bean
    public Sheets sheetsService() throws Exception {

        // 인증파일 (FCM과 동일한 방식으로 환경변수에서 파일 경로 읽기)
        InputStream input = new FileInputStream(serviceAccountFilePath);

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
