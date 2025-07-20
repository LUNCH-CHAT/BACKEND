package com.lunchchat.global.apiPayLoad.exception;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;

public class AuthException extends GeneralException {

  public AuthException(BaseErrorCode errorCode) {
    super(errorCode);
  }
}
