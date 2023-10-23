package com.craft.authentication.exception;

import com.craft.authentication.dto.Error;

public class CustomException extends RuntimeException{
    Error error;
    public CustomException(String message, String errorCode){
        this.error = new Error(message, errorCode);
    }
    public CustomException(Error error){
        this.error = error;
    }
}
