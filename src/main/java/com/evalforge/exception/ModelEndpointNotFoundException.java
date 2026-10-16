package com.evalforge.exception;

import java.util.Map;

import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;

public class ModelEndpointNotFoundException extends BusinessException{

    private final Long id;

    public ModelEndpointNotFoundException(long id){ 
            super(ErrorCode.RESOURCE_NOT_FOUND,"Model Not Found",Map.of("id",id),null);
            this.id =id;
    }

    public Long getId(){
        return id;
    }

}