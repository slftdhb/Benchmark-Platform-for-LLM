package com.evalforge.benchmark.exception;


public class BenchmarkImportException extends RuntimeException{

        private final int lineNo;


    public BenchmarkImportException(Integer lineNo,String exMessage) {
        super("Jsonl validation failed at line : " + lineNo + ":" + exMessage);
        this.lineNo =lineNo; 
    }

    //用于保留数据库的原始异常
    public BenchmarkImportException(int lineNo,String message,Throwable cause){
        super("JSONL validation failed at line "
                + lineNo + ": " + message, cause);
        this.lineNo=lineNo;
    
    }

    public int getLineNo(){
        return this.lineNo;
    }
}