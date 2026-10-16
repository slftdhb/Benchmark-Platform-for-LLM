package com.evalforge.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


import com.evalforge.common.error.ApiError;
import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;


import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler{
    private static final Logger log =LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(
        BusinessException ex,
        HttpServletRequest request
    ){
        ErrorCode code =ex.getErrorCode();
        ApiError body =new ApiError(
            code.name(),
            ex.getMessage(),
            requestId(request),
            Instant.now(),
            ex.getDetails()
        );

        log.warn("Business request failed,code={},details={},message={}",
                 ex.getErrorCode(),
                 ex.getDetails(),
                 ex.getMessage()
        );

        return ResponseEntity.status(code.getStatus()).body(body);

    }
    
    @ExceptionHandler (MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ){
            Map<String,Object> details = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(error->{
                    details.put(error.getField(),
                                error.getDefaultMessage());
            });

            ApiError body = new ApiError(
                ErrorCode.VALIDATION_FAILED.name(),
                "Request Valid failed",
                requestId(request),
                Instant.now(),
                details
            );

            return ResponseEntity.badRequest().body(body);
    }


    private String requestId(HttpServletRequest request){
        Object value = request.getAttribute("requestId");

        return value instanceof String  id ? id : "unknown";
    }

}