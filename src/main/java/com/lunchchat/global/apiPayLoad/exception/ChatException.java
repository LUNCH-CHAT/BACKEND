package com.lunchchat.global.apiPayLoad.exception;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;

public class ChatException extends GeneralException {

    public ChatException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}