package com.evalforge.demo;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service 
public class RedisDemo{
    private final StringRedisTemplate redisTemplate;

    public RedisDemo(StringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    public void write(){
        redisTemplate.opsForValue().set(
            "test:hello",
            "Evalforge",
            Duration.ofMinutes(60)
        );
    }

    public String read(){
        return redisTemplate.opsForValue().get("test:hello");
    }
    
}