package com.craft.userAuth.exception;

import com.craft.userAuth.dto.Error;

public class CustomException extends RuntimeException{
    Error error;

    public CustomException(String message, String errorCode){
        this.error = new Error(message, errorCode);
    }

    public CustomException(Error error){
        this.error = error;
    }
}
