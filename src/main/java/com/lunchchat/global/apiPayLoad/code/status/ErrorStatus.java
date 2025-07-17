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
  BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND,"COMMON404","찾을 수 없는 요청입니다"),

  //User Error
  USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER_401","로그인 유저를 찾을 수 없습니다"),

  // Match Error
  INVALID_MATCH_STATUS(HttpStatus.BAD_REQUEST, "MATCH_400", "유효하지 않은 매칭 상태입니다."),
  INVALID_MATCH_REQUEST(HttpStatus.BAD_REQUEST, "MATCH_400", "유효하지 않은 매칭 요청입니다."),
  SELF_MATCH_REQUEST(HttpStatus.BAD_REQUEST, "MATCH_400", "자기 자신에게 매칭 요청을 보낼 수 없습니다."),
  ALREADY_MATCHED(HttpStatus.BAD_REQUEST, "MATCH_400", "이미 매칭 이력이 존재합니다."),
  INVALID_MATCH_ID(HttpStatus.BAD_REQUEST, "MATCH_400", "유효하지 않은 매칭 ID입니다."),
  MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH_404", "매칭을 찾을 수 없습니다.");

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
            .build()
            ;
  }
}