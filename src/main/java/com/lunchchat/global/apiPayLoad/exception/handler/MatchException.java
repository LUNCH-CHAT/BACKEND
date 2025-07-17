package com.lunchchat.global.apiPayLoad.exception.handler;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;
import com.lunchchat.global.apiPayLoad.exception.GeneralException;

public class MatchException extends GeneralException {
  public MatchException(BaseErrorCode errorCode) {
    super(errorCode);
  }
}