package com.gatewayserver.exception;


import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ErrorHandlingHelper{

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(UNAUTHORIZED)
    public String usernameNotFoundExceptionHandler(UsernameNotFoundException ex){
        log.error(ex.getMessage());
        return ex.getMessage();
    }


    @ExceptionHandler(TokenException.class)
    @ResponseStatus(BAD_REQUEST)
    public String tokenExceptionHandler(TokenException ex){
        log.error(ex.getMessage());
        return ex.getMessage();
    }
    @ExceptionHandler(WebClientResponseException.class)
    protected ResponseEntity<Object> handleBadRequestException(WebClientResponseException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
    }
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public String badRequestExceptionHandler(BadRequestException ex){
        log.error(ex.getMessage());
        return ex.getMessage();
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(BAD_REQUEST)
    public String handleMalformedJwtException(MalformedJwtException ex){
        log.error(ex.getMessage());
        return "Invalid data";
    }

}
