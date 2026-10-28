package com.evalforge.ratelimit;

import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.evalforge.cache.CacheKey;


@Service
public class RedisModelRateLimiter implements ModelRateLimiter {
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<List> script;
    
    public RedisModelRateLimiter(StringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
        //加载初始化lua脚本
        this.script=new DefaultRedisScript<>();
        this.script.setLocation(new ClassPathResource("redis/fixed_window_rate_limit.lua"));
        this.script.setResultType(List.class);

    }

    @Override 
    public RateLimitResult acquire(Long ModelId){
            //固定窗口限流，执行脚本，约定60秒只允许5次请求
            List<?> values =redisTemplate.execute(script,List.of(CacheKey.modelRateLimit(ModelId)), "5","60");

            if(values ==null || values.size()!= 3){
                throw new IllegalStateException("Invalid rate limit script result");
            }
            boolean allowed = ((Number)values.get(0)).longValue() == 1;
            long remaining = ((Number)values.get(1)).longValue();
            long windowSeconds =((Number)values.get(2)).longValue();

            return new RateLimitResult(allowed,remaining,windowSeconds);
    }
}
