package com.lunchchat.global.apiPayLoad.exception.handler;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;
import com.lunchchat.global.apiPayLoad.exception.GeneralException;

public class TimeTableException extends GeneralException {
  public TimeTableException(BaseErrorCode errorCode) {
    super(errorCode);
  }
}