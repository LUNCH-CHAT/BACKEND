package com.lunchchat.global.apiPayLoad.exception.handler;

import com.lunchchat.global.apiPayLoad.code.BaseErrorCode;
import com.lunchchat.global.apiPayLoad.exception.GeneralException;

public class ChatException extends GeneralException {

    public ChatException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}