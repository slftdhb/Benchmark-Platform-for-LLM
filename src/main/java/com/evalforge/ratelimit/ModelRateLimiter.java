package com.evalforge.ratelimit;

public interface ModelRateLimiter {
        //根据模型id确定某个模型限流数量
        RateLimitResult acquire(Long modelId);
}
