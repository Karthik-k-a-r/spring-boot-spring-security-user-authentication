package com.craft.authentication.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {
    private String code;
    private String message;

    public Error(String message, String errorCode){
        this.message = message;
        this.code = errorCode;
    }

    public Error(String message){
        this.message = message;
    }
}
