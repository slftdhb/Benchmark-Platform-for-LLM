package com.evalforge.benchmark.exception;

import java.util.Map;

import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;

public class BenchmarkImportException extends BusinessException{

        private final int lineNo;


    public BenchmarkImportException(Integer lineNo,String exMessage) {
            this(lineNo,exMessage,null);
    }

    //用于保留数据库的原始异常
    public BenchmarkImportException(int lineNo,String message,Throwable cause){
            super(ErrorCode.BENCHMARK_IMPORT_INVALID,message,Map.of("lineNo",lineNo),cause);
            this.lineNo=lineNo;
    }
    public BenchmarkImportException(ErrorCode errorCode,int lineNo, String message){
            super(errorCode,message,Map.of("lineNo",lineNo),null);
            this.lineNo=lineNo;
    }

    public BenchmarkImportException(ErrorCode code,int lineNo,String message,Throwable cause){
        super(
            code,
            message,
            Map.of("lineNo",lineNo),
            cause
        );
        this.lineNo=lineNo;
    }

    public int getLineNo(){
        return this.lineNo;
    }
}