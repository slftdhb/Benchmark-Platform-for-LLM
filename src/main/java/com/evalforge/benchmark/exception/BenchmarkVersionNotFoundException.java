package com.evalforge.benchmark.exception;

import java.util.Map;

import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;

public class BenchmarkVersionNotFoundException extends BusinessException{
    private final Long id;
    public BenchmarkVersionNotFoundException(Long id) {
        super(ErrorCode.RESOURCE_NOT_FOUND,"BenchmarkVersion Not Found",Map.of("id",id),null);
        this.id=id;
    }
    public BenchmarkVersionNotFoundException(Long id, String message){
        super(ErrorCode.RESOURCE_NOT_FOUND,message,Map.of("id",id),null);
        this.id=id;
    }
    public BenchmarkVersionNotFoundException(Long id,String message,Throwable cause){
        super(ErrorCode.RESOURCE_NOT_FOUND,message,Map.of("id",id),cause);
        this.id=id;
    }
    public BenchmarkVersionNotFoundException(ErrorCode errorCode,Long id,String message,Throwable cause){
        super(errorCode,message,Map.of("id",id),cause);
        this.id=id;
    }

    public Long getId(){
        return id;
    }
}