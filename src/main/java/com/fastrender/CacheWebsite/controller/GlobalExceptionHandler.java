package com.fastrender.CacheWebsite.controller;

import com.fastrender.CacheWebsite.Dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handlerRuntimeException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.datetime = LocalDateTime.now();
        errorResponse.message = e.getMessage();
        errorResponse.details = e.getStackTrace().toString();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}