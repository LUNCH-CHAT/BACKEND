package com.lunchchat.global.apiPayLoad.code.status;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;
import com.lunchchat.global.apiPayLoad.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

  //Common Error
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "찾을 수 없는 요청입니다"),

  //User Error
  INTEREST_MAX_THREE(HttpStatus.BAD_REQUEST,"USER_400_INTEREST","관심사 정보는 최대 3개입니다"),
  USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER_401","로그인 유저를 찾을 수 없습니다"),
  USER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "USER_403", "권한이 없습니다."),
  USER_STATISTICS_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404", "유저 통계 정보를 찾을 수 없습니다."),
  COLLEGE_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_COLLEGE", "단과대학 정보를 찾을 수 없습니다."),
  DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_DEPT", "학과 정보를 찾을 수 없습니다."),
  INTEREST_NOT_FOUND(HttpStatus.NOT_FOUND,"USER_404_INTEREST","관심사 정보를 찾을 수 없습니다."),
  DUPLICATE_STUDENTNO(HttpStatus.CONFLICT, "USER_409", "이미 등록된 학번입니다."),

  //SignUp Error
  INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_400_EMAIL", "이메일 형식이 올바르지 않습니다."),
  ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "AUTH_403_DISABLED", "계정이 비활성화 상태입니다."),
  EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "AUTH_403_EMAIL_UNVERIFIED", "이메일이 인증되지 않았습니다."),
  EMAIL_EXISTS(HttpStatus.CONFLICT, "AUTH_409", "이미 사용 중인 이메일입니다."),

  UNIVERSITY_NOT_FOUND(HttpStatus.BAD_REQUEST,"AUTH_401_UNIVERSITY","대학교를 찾을 수 없습니다"),


  ////Token Error
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_401_ACCESS_EXPIRED", "인증 토큰이 만료되었습니다."),
  TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "TOKEN_401_ACCESS_MALFORMED", "잘못된 형식의 인증 토큰입니다."),

  JWT_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_401", "유효하지 않은 JWT 토큰입니다."),
  JWT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JWT_500", "JWT 생성에 실패했습니다."),

  REFRESH_TOKEN_MISSING(HttpStatus.BAD_REQUEST,"REFRESH_400","리프레시 토큰이 쿠키에 없습니다"),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"REFRESH_401","리프레시 토큰이 유효하지 않습니다"),
  REUSED_REFRESH_TOKEN(HttpStatus.FORBIDDEN,"REFRESH_403","이미 사용된 리프레시토큰입니다"),

  // Match Error
  INVALID_MATCH_STATUS(HttpStatus.BAD_REQUEST, "MATCH_400", "유효하지 않은 매칭 상태입니다."),
  INVALID_MATCH_REQUEST(HttpStatus.BAD_REQUEST, "MATCH_400", "유효하지 않은 매칭 요청입니다."),
  SELF_MATCH_REQUEST(HttpStatus.BAD_REQUEST, "MATCH_400", "자기 자신에게 매칭 요청을 보낼 수 없습니다."),
  ALREADY_MATCHED(HttpStatus.BAD_REQUEST, "MATCH_400", "이미 매칭 이력이 존재합니다."),
  INVALID_MATCH_ID(HttpStatus.BAD_REQUEST, "MATCH_400", "유효하지 않은 매칭 ID입니다."),
  MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH_404", "매칭을 찾을 수 없습니다."),

  // Interest Error
  INTERESTS_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "INTEREST_400", "관심사는 최대 3개까지만 선택할 수 있습니다."),
  INTERESTS_NOT_FOUND(HttpStatus.NOT_FOUND, "INTEREST_404", "선택한 관심사를 찾을 수 없습니다."),

  // Keyword Error
  KEYWORDS_COUNT_MISMATCH(HttpStatus.BAD_REQUEST, "KEYWORD_400", "키워드는 3개여야 합니다."),
  DUPLICATE_KEYWORD_TYPE(HttpStatus.BAD_REQUEST, "KEYWORD_400_DUPLICATE", "키워드 타입이 중복되었습니다."),
  KEYWORD_TYPE_REQUIRED(HttpStatus.BAD_REQUEST, "KEYWORD_400_TYPE_REQUIRED", "키워드 타입은 필수입니다."),

  // Chat Error
  CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_404", "해당 채팅방이 존재하지 않습니다."),
  CANNOT_CHAT_WITH_SELF(HttpStatus.BAD_REQUEST, "CHAT_400", "자기 자신과는 채팅할 수 없습니다."),
  UNAUTHORIZED_CHATROOM_ACCESS(HttpStatus.FORBIDDEN, "CHAT_403", "해당 채팅방에 접근할 수 있는 사용자가 아닙니다."),
  NO_MESSAGES_IN_CHATROOM(HttpStatus.NOT_FOUND, "CHAT_404", "해당 채팅방에 메시지가 존재하지 않습니다."),
  CHATROOM_EXITED(HttpStatus.FORBIDDEN, "CHAT_200", "퇴장한 채팅방입니다."),

  // FCM Error
  FCM_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FCM500", "FCM 메시지 전송에 실패했습니다."),
  FCM_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "FCM404", "해당 사용자의 FCM 토큰을 찾을 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  @Override
  public ErrorReasonDTO getReason() {
    return ErrorReasonDTO.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .build();
  }

  @Override
  public ErrorReasonDTO getReasonHttpStatus() {
    return ErrorReasonDTO.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .httpStatus(httpStatus)
            .build();
  }
}
