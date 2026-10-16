package com.evalforge.exceptionhandle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

import com.evalforge.benchmark.exception.BenchmarkImportException;
import com.evalforge.benchmark.exception.BenchmarkVersionNotFoundException;
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

    @ExceptionHandler(BenchmarkImportException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> handleBenchmarkImportException(BenchmarkImportException ex){
        return Map.of(
                    "status",400,
                    "error","Import Error",
                    "lineNo",ex.getLineNo(),
                    "message",ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,Object> handleIllegalArgumentException(IllegalArgumentException ex){

        return Map.of(
                    "status",400,
                    "error","Bad Request",  
                    "message",ex.getMessage()
        );
    }

    @ExceptionHandler(BenchmarkVersionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,Object> handleBenchmarkVersionNotFoundException(BenchmarkVersionNotFoundException ex){
        return Map.of(
                    "status",404,
                    "error","Not Found",
                    "message",ex.getMessage()
        );
    }
}