package com.craft.userAuth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
