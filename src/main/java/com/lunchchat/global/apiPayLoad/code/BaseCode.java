package com.lunchchat.global.apiPayLoad.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
  HttpStatus getStatus();
  String getCode();
  String getMessage();
}
