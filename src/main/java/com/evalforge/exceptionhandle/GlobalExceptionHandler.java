package com.evalforge.exceptionhandle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import com.evalforge.exception.ModelEndpointNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(ModelEndpointNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)

    public Map<String,Object> handleNotFound(ModelEndpointNotFoundException ex){

        return Map.of(
                    "status", 404 ,
                    "error", "Not Found",
                    "message",  ex.getMessage()
        );
    }
}