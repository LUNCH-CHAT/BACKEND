package com.lunchchat.global.apiPayLoad.exception.handler;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;
import com.lunchchat.global.apiPayLoad.exception.GeneralException;

public class MemberHandler extends GeneralException {
  public MemberHandler(BaseErrorCode errorCode) {
    super(errorCode);
  }
}