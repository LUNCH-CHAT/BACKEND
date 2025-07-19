package com.lunchchat.global.apiPayLoad.exception;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;

public class InvalidTokenException extends GeneralException {

  public InvalidTokenException(BaseErrorCode errorCode) {
    super(errorCode);
  }

}
