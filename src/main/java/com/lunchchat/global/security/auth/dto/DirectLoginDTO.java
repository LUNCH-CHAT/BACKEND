package com.lunchchat.global.security.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DirectLoginDTO() {

    public record Request(
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "유효한 이메일 형식이어야 합니다.")
            String email,
            
            @NotBlank(message = "비밀번호는 필수입니다.")
            String password
    ) {}

    public record Response(
            String accessToken,
            String refreshToken,
            String email,
            String membername,
            boolean isNewUser
    ) {}
}