package com.lunchchat.global.apiPayLoad.exception;

import com.lunchchat.global.apiPayLoad.ApiResponse;
import com.lunchchat.global.apiPayLoad.code.ErrorReasonDTO;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

  // 파라미터 예외 처리
  @ExceptionHandler
  public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {

    String errorMessage = e.getConstraintViolations().stream()
        .map(constraintViolation -> constraintViolation.getMessage())
        .findFirst()
        .orElse("Invalid input");

    ErrorStatus errorStatus = resolveErrorStatus(errorMessage, ErrorStatus.BAD_REQUEST);

    return handleExceptionInternalConstraint(e, errorStatus, HttpHeaders.EMPTY, request);
  }

  // RequestBody 검증 실패
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
      String fieldName = fieldError.getField();
      String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
      errors.merge(fieldName, errorMessage, (existing, newMsg) -> existing + ", " + newMsg);
    });

    ApiResponse<Object> body = ApiResponse.onFailure(ErrorStatus.BAD_REQUEST.getCode(),
        ErrorStatus.BAD_REQUEST.getMessage(),
        errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(body);
  }

  // 기타 예외 -> 서버 내부 에러
  @ExceptionHandler
  public ResponseEntity<Object> exception(Exception e, WebRequest request) {

    return handleExceptionInternalFalse(
        e,
        ErrorStatus.INTERNAL_SERVER_ERROR,
        HttpHeaders.EMPTY,
        ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus(),
        request,
        e.getMessage()
    );
  }

  // 비즈니스 예외처리
  @ExceptionHandler(GeneralException.class)
  public ResponseEntity<Object> onThrowException(GeneralException generalException, HttpServletRequest request) {

    ErrorReasonDTO errorReason = generalException.getErrorReasonHttpStatus();
    return handleExceptionInternal(generalException, errorReason, null, request);
  }

  private ResponseEntity<Object> handleExceptionInternal(
      Exception e, ErrorReasonDTO reason, HttpHeaders headers, HttpServletRequest request) {
    ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(), reason.getMessage(), null);
    WebRequest webRequest = new ServletWebRequest(request);

    return super.handleExceptionInternal(e, body, headers, reason.getHttpStatus(), webRequest);
  }

  private ResponseEntity<Object> handleExceptionInternalFalse(
      Exception e, ErrorStatus errorStatus, HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
    ApiResponse<Object> body = ApiResponse.onFailure(errorStatus.getCode(), errorStatus.getMessage(), errorPoint);

    return super.handleExceptionInternal(e, body, headers, status, request);
  }

  private ResponseEntity<Object> handleExceptionInternalArgs(
      Exception e, HttpHeaders headers, ErrorStatus errorStatus, WebRequest request, Map<String, String> errorArgs) {
    ApiResponse<Object> body = ApiResponse.onFailure(errorStatus.getCode(), errorStatus.getMessage(), errorArgs);

    return super.handleExceptionInternal(e, body, headers, errorStatus.getHttpStatus(), request);
  }

  private ResponseEntity<Object> handleExceptionInternalConstraint(
      Exception e, ErrorStatus errorStatus, HttpHeaders headers, WebRequest request) {
    ApiResponse<Object> body = ApiResponse.onFailure(errorStatus.getCode(), errorStatus.getMessage(), null);

    return super.handleExceptionInternal(e, body, headers, errorStatus.getHttpStatus(), request);
  }

  private ErrorStatus resolveErrorStatus(String errorMessage, ErrorStatus defaultStatus) {
    try {
      return ErrorStatus.valueOf(errorMessage);
    } catch (IllegalArgumentException ex) {
       return defaultStatus;
    }
  }
}
