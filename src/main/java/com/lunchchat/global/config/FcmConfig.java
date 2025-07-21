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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Slf4j
@Configuration
public class FcmConfig {

    @Value("${fcm.service-account-json:}")
    private String serviceAccountJson;
    
    @Value("${fcm.service-account-json-base64:}")
    private String serviceAccountJsonBase64;
    
    @Value("${fcm.service-account-file:}")
    private String serviceAccountFilePath;

    private String projectId;
    private String privateKey;
    private String clientEmail;

    private FirebaseApp firebaseApp;

    @PostConstruct
    public void initialize() {
        try {
            String actualServiceAccountJson = getServiceAccountJson();
            
            if (actualServiceAccountJson == null || actualServiceAccountJson.trim().isEmpty()) {
                log.warn("FCM 서비스 계정 정보가 설정되지 않았습니다. FCM 기능을 사용할 수 없습니다.");
                return;
            }
            
            parseServiceAccountJson(actualServiceAccountJson);
            validateConfiguration();

            InputStream serviceAccountStream = new ByteArrayInputStream(
                actualServiceAccountJson.getBytes(StandardCharsets.UTF_8)
            );

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .setProjectId(projectId)
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
                log.info("FirebaseApp 초기화 성공 (project: {})", projectId);
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
                log.info("기존 FirebaseApp 인스턴스 사용");
            }
        } catch (Exception e) {
            log.error("FirebaseApp 초기화 실패", e);
            throw new RuntimeException("FCM 초기화 실패", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        if (firebaseApp == null) {
            log.warn("FirebaseApp이 초기화되지 않았습니다. FCM 기능을 사용할 수 없습니다.");
            return null;
        }
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    private String getServiceAccountJson() {
        // 1. 파일 경로에서 읽기 (EC2 환경)
        if (!serviceAccountFilePath.isEmpty()) {
            try {
                String content = Files.readString(Paths.get(serviceAccountFilePath), StandardCharsets.UTF_8);
                log.info("🖥️  FCM JSON을 EC2 파일에서 읽었습니다: {}", serviceAccountFilePath);
                return content;
            } catch (IOException e) {
                log.error("❌ FCM JSON 파일을 읽을 수 없습니다: {}", serviceAccountFilePath, e);
            }
        }
        
        // 2. Base64 디코딩 (백업 방식)
        if (!serviceAccountJsonBase64.isEmpty()) {
            try {
                String decoded = new String(
                    Base64.getDecoder().decode(serviceAccountJsonBase64),
                    StandardCharsets.UTF_8
                );
                log.info("🔓 FCM JSON Base64 디코딩 완료");
                return decoded;
            } catch (Exception e) {
                log.error("❌ FCM JSON Base64 디코딩 실패", e);
            }
        }
        
        // 3. 직접 JSON 문자열 (로컬 환경)
        if (!serviceAccountJson.isEmpty()) {
            log.info("🏠 FCM JSON을 로컬 환경변수에서 읽었습니다");
            return serviceAccountJson;
        }
        
        log.warn("⚠️  FCM 서비스 계정 정보를 찾을 수 없습니다. 모든 방식을 확인했습니다.");
        return null;
    }

    private void parseServiceAccountJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);

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
