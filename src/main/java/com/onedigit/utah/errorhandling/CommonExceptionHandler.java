package com.onedigit.utah.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@Component
@ControllerAdvice
public class CommonExceptionHandler{
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleWebClientException(Exception ex){
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body("error");
    }
}