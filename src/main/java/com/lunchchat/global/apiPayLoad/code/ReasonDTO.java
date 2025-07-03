package com.lunchchat.global.apiPayLoad.code;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class ReasonDTO {

  private HttpStatus httpStatus;

  private final boolean isSuccess;
  private final String code;
  private final String message;

  public boolean getIsSuccess(){return isSuccess;}

}