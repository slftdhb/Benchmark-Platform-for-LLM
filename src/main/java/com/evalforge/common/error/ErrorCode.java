package com.evalforge.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
            VALIDATION_FAILED(HttpStatus.BAD_REQUEST),
            RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
            BENCHMARK_IMPORT_INVALID(HttpStatus.BAD_REQUEST),
            BENCHMARK_VERSION_CONFLICT(HttpStatus.CONFLICT),
            DUPLICATE_RESOURCE(HttpStatus.CONFLICT),
            INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

            private final HttpStatus status;

            ErrorCode(HttpStatus status){
                this.status=status;
            }
            public HttpStatus getStatus(){
                return status;
            }
}
