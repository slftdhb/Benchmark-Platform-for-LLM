package com.evalforge.ratelimit;

import java.util.Map;

import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;

public class ModelRateLimitException extends BusinessException{

    public ModelRateLimitException(Long modelId,long retryAfterSeconds){
            
            super(ErrorCode.MODEL_RETE_LIMITED,"Model request rate limit exceeded",
                Map.of(
                    "modelId",modelId,
                    "retryAfterSeconds",retryAfterSeconds
                )
            );
    }
}
