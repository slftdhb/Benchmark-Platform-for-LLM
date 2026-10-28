package com.evalforge.idempotency;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.evalforge.cache.CacheKey;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class RedisIdempotencyCache {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisIdempotencyCache(StringRedisTemplate redisTemplate,ObjectMapper objectMapper){
        this.redisTemplate=redisTemplate;
        this.objectMapper=objectMapper;
    }

    //拿到redis的结果字符串并转成record
    public IdempotencyCacheEntry get(String key){
        String entry =redisTemplate.opsForValue().get(CacheKey.benchmark_create_idempotency_key(key));
        
        if(entry == null ){
            return null;
        }

        //直接反序列化,同时进行完整性校验
        IdempotencyCacheEntry result =objectMapper.readValue(entry,IdempotencyCacheEntry.class);
        if(result == null | result.requestHash().isBlank() | result.requestHash() == null | result.resourceId() == null ){
            return null;
        }
        return result;
    }

    public void put(String key, IdempotencyCacheEntry entry){
            String redisEntry = objectMapper.writeValueAsString(entry);
            redisTemplate.opsForValue().set(
                CacheKey.benchmark_create_idempotency_key(key),
                redisEntry,
                Duration.ofMinutes(30)
            );
    }
}
