package com.lunchchat.global.apiPayLoad.exception.handler;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;
import com.lunchchat.global.apiPayLoad.exception.GeneralException;

public class MatchHandler extends GeneralException {
  public MatchHandler(BaseErrorCode errorCode) {
    super(errorCode);
  }
}