package com.lunchchat.global.apiPayLoad.code.status;

import com.lunchchat.global.apiPayLoad.code.BaseCode;
import com.lunchchat.global.apiPayLoad.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 일반적인 응답
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    // FCM 관련 응답
    FCM_TOKEN_UPDATE_SUCCESS(HttpStatus.OK, "FCM200", "FCM 토큰이 성공적으로 업데이트되었습니다."),

    // 관심사 관련 응답
    INTERESTS_UPDATE_SUCCESS(HttpStatus.OK, "INTERESTS200", "관심사가 성공적으로 업데이트되었습니다."),

    // 키워드 관련 응답
    KEYWORDS_UPDATE_SUCCESS(HttpStatus.OK, "KEYWORDS200", "키워드가 성공적으로 업데이트되었습니다."),

    // 로그인 응답
    USER_LOGIN_OK(HttpStatus.OK, "USER200", "유저 로그인 성공"),
    USER_SIGNUP_OK(HttpStatus.OK, "USER201", "유저 회원가입 성공");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
            .message(message)
            .code(code)
            .isSuccess(true)
            .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
            .message(message)
            .code(code)
            .isSuccess(true)
            .httpStatus(httpStatus)
            .build()
            ;
    }
}