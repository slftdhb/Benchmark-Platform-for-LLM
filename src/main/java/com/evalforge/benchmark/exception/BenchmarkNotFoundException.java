package com.evalforge.benchmark.exception;

import java.util.Map;

import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;

public class BenchmarkNotFoundException extends BusinessException {
    private final Long id; 

    public BenchmarkNotFoundException(ErrorCode errorCode, Long id ,String message,Throwable cause) {
            super(errorCode,message,Map.of("id",id),cause);
            this.id=id;
    }
    public BenchmarkNotFoundException(Long id,String message){
            super(ErrorCode.RESOURCE_NOT_FOUND,message,Map.of("id",id),null);
            this.id=id;
    }
    public BenchmarkNotFoundException(Long id){
        super(ErrorCode.RESOURCE_NOT_FOUND,"Benchmark Not Found",Map.of("id",id),null);
        this.id=id;
    }

    public BenchmarkNotFoundException(Long id,String message ,Throwable cause){
            super(ErrorCode.RESOURCE_NOT_FOUND,message,Map.of("id",id),cause);
            this.id=id;
    }

    public Long getId(){
        return this.id;
    }
}