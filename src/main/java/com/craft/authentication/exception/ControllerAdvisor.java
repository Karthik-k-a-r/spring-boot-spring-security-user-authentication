package com.craft.authentication.exception;

import com.craft.authentication.constants.Constants;
import com.craft.authentication.dto.Error;
import com.craft.authentication.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Object>  handleTypeMismatchException(TypeMismatchException ex) {
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getMessage(),Constants.ErrorCodes.BAD_GATEWAY));

        ErrorResponse errorResponse  = ErrorResponse.builder()
                .errors(errors)
                .timeStamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object>  handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getMessage(),Constants.ErrorCodes.BAD_GATEWAY));

        ErrorResponse errorResponse  = ErrorResponse.builder()
                .errors(errors)
                .timeStamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object>  handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<Error> errors = ex.getBindingResult()
                        .getFieldErrors()
                                .stream()
                                        .map(x -> {
                                            if(x.getRejectedValue() != null) {
                                                return new Error((x.getField() + " : " + x.getDefaultMessage() + " : "+x.getRejectedValue()),Constants.ErrorCodes.BAD_REQUEST);
                                            } else {
                                                return new Error((x.getField() + " : " + x.getDefaultMessage()),Constants.ErrorCodes.BAD_REQUEST);
                                            }
                                        })
                .collect(Collectors.toList());

        ErrorResponse errorResponse  = ErrorResponse.builder()
                .errors(errors)
                .timeStamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException ex){

        List<Error> errors = new ArrayList<>();
        errors.add(ex.error);

        ErrorResponse errorResponse  = ErrorResponse.builder()
                .errors(errors)
                .timeStamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {

        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getMessage(),Constants.ErrorCodes.UNAUTHORIZED_CODE));

        ErrorResponse errorResponse  = ErrorResponse.builder()
                .errors(errors)
                .timeStamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<Object> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex) {
        CustomException customException = (CustomException) ex.getCause();
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(customException.error.getMessage(),Constants.ErrorCodes.UNAUTHORIZED_CODE));

        ErrorResponse errorResponse  = ErrorResponse.builder()
                .errors(errors)
                .timeStamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex){

        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getMessage(),Constants.ErrorCodes.INTERNAL_SERVER_ERROR_CODE));

        ErrorResponse errorResponse  = ErrorResponse.builder()
                .errors(errors)
                .timeStamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex){

        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getMessage(),Constants.ErrorCodes.FORBIDDEN_CODE));

        ErrorResponse errorResponse  = ErrorResponse.builder()
                .errors(errors)
                .timeStamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, HttpServletResponse response){

        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getMessage(),Constants.ErrorCodes.FORBIDDEN_CODE));

        ErrorResponse errorResponse  = ErrorResponse.builder()
                .errors(errors)
                .timeStamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

}
