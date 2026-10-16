package com.evalforge.common.error;

import java.util.Map;

public class BusinessException  extends RuntimeException{

            //让业务中的异常全部继承这个类
            private final ErrorCode errorCode;
            private final Map<String,Object> details;

            public BusinessException(ErrorCode errorCode,String message){
                this(errorCode, message, Map.of(),null);
            }   

            public BusinessException(ErrorCode errorCode,String message,Map<String,Object> details){
                this(errorCode, message,details,null);
            }

            public BusinessException(ErrorCode errorCode,String message, Map<String,Object> details,Throwable cause){
                super(message,cause);
                this.details= details == null ? Map.of() : Map.copyOf(details);
                this.errorCode=errorCode;
            }

            public ErrorCode getErrorCode(){
                return errorCode;
            }
            public Map<String,Object> getDetails(){
                return details;
            }
    
}
