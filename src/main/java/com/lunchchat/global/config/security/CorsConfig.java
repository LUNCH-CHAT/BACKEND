package com.lunchchat.global.config.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용 오리진 목록
        configuration.setAllowedOrigins(
            List.of("http://localhost:8080", "https://lunchchat.vercel.app",
                "https://develop-lunchchat.vercel.app", "http://localhost:5173", "https://lunchchat.kro.kr"));
        configuration.addAllowedHeader("*");
        configuration.setExposedHeaders(List.of("access", "Authorization"));
        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 자격증명 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
