package com.craft.userAuth.exception;

import com.craft.userAuth.constants.Constants;
import com.craft.userAuth.dto.Error;
import com.craft.userAuth.dto.ErrorResponse;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerAdvisor  {


    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Object>  handleTypeMismatchException(TypeMismatchException typeMismatchException) {

        ErrorResponse errorResponse  = new ErrorResponse();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object>  httpMessageNotReadableException(HttpMessageNotReadableException ex) {

        ErrorResponse errorResponse  = new ErrorResponse();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object>  methodArgumentNotValidException(MethodArgumentNotValidException ex) {

        ErrorResponse errorResponse  = new ErrorResponse();

        List<Error> errors = ex.getBindingResult()
                        .getFieldErrors()
                                .stream()
                                        .map(x -> {
                                            if(x.getRejectedValue() != null) {
                                                return new Error(x.getField() + " : " + x.getDefaultMessage() + " : "+x.getRejectedValue());
                                            } else {
                                                return new Error(x.getField() + " : " + x.getDefaultMessage());
                                            }
                                        })
                .collect(Collectors.toList());

        errorResponse.setErrors(errors);
        errorResponse.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> customException(CustomException ex){

        ErrorResponse errorResponse  = new ErrorResponse();

        List<Error> errors = new ArrayList<>();
        errors.add(ex.error);
        errorResponse.setErrors(errors);
        errorResponse.setTimeStamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> badCredentialsException(BadCredentialsException e) {
        ErrorResponse errorResponse  = new ErrorResponse();

        List<Error> errors = new ArrayList<>();
        errors.add(new Error(e.getMessage()));
        errorResponse.setErrors(errors);
        errorResponse.setTimeStamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<Object> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        CustomException customException = (CustomException) e.getCause();
        ErrorResponse errorResponse  = new ErrorResponse();

        List<Error> errors = new ArrayList<>();
        errors.add(customException.error);
        errorResponse.setErrors(errors);
        errorResponse.setTimeStamp(LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex){
        ErrorResponse errorResponse  = new ErrorResponse();
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(Constants.ErrorMessages.INTERNAL_SERVER_ERROR,Constants.ErrorCodes.INTERNAL_SERVER_ERROR_CODE));
        errorResponse.setErrors(errors);
        errorResponse.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> exception(NullPointerException ex){
        ErrorResponse errorResponse  = new ErrorResponse();
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getMessage(),ex.getMessage()));
        errorResponse.setErrors(errors);
        errorResponse.setTimeStamp(LocalDateTime.now());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
