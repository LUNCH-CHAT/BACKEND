package com.lunchchat.global.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FcmConfig {

    @Value("${fcm.service-account-json}")
    private String serviceAccountJson;

    private String projectId;
    private String privateKey;
    private String clientEmail;

    private FirebaseApp firebaseApp;

    @PostConstruct
    public void initialize() {
        try {
            parseServiceAccountJson();
            validateConfiguration();

            InputStream serviceAccountStream = new ByteArrayInputStream(
                serviceAccountJson.getBytes(StandardCharsets.UTF_8)
            );

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .setProjectId(projectId)
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
                log.info("FirebaseApp 초기화 성공");
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
                log.info("기존 FirebaseApp 인스턴스 사용");
            }
        } catch (IOException e) {
            log.error("FirebaseApp 초기화 실패", e);
            throw new RuntimeException("FCM 초기화 실패", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    private void parseServiceAccountJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(serviceAccountJson);

            this.projectId = rootNode.path("project_id").asText();
            this.privateKey = rootNode.path("private_key").asText();
            this.clientEmail = rootNode.path("client_email").asText();

            log.debug("FCM 서비스 계정 JSON 파싱 완료");
        } catch (IOException e) {
            log.error("FCM 서비스 계정 JSON 파싱 실패", e);
            throw new IllegalArgumentException("FCM 서비스 계정 JSON 형식이 올바르지 않습니다.", e);
        }
    }

    private void validateConfiguration() {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("FCM project-id가 설정되지 않았습니다");
        }
        if (privateKey == null || privateKey.trim().isEmpty()) {
            throw new IllegalArgumentException("FCM private-key가 설정되지 않았습니다");
        }
        if (clientEmail == null || clientEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("FCM client-email이 설정되지 않았습니다");
        }
        log.debug("FCM 환경변수 검증 완료");
    }
}
