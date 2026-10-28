package com.evalforge.ratelimit;

public record RateLimitResult(
        //分别表示，是否放行，剩余请求次数，建议等待尝试秒数
        boolean allowed,
        long remaining,
        long retryAfterSeconds
) {
    
}
