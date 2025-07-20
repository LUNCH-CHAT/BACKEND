package com.lunchchat.global.apiPayLoad.exception;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;
import com.lunchchat.global.apiPayLoad.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
//@AllArgsConstructor
public class GeneralException extends RuntimeException {

  private BaseErrorCode code;

  public GeneralException(BaseErrorCode code) {
    super(code.getReason().getMessage());
    this.code = code;
  }

  public ErrorReasonDTO getErrorReason() {
    return code.getReason();
  }

  public ErrorReasonDTO getErrorReasonHttpStatus(){
    return code.getReasonHttpStatus();
  }
}