package com.lunchchat.global.apiPayLoad.code;

public interface BaseErrorCode {
  ErrorReasonDTO getReason();

  ErrorReasonDTO getReasonHttpStatus();
}
