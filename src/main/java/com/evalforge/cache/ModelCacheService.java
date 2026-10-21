package com.evalforge.cache;

import java.time.Duration;

import org.apache.catalina.webresources.Cache;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service 
public class ModelCacheService{
        private final StringRedisTemplate redisTemplate;

        public ModelCacheService(StringRedisTemplate redisTemplate){
            this.redisTemplate=redisTemplate;
        }

        public String get(Long id){
            return redisTemplate.opsForValue().get(CacheKey.model(id));
        }

        //往缓存放
        public void put(Long id,String json){
            redisTemplate.opsForValue().set(
                CacheKey.model(id),
                json,
                Duration.ofMinutes(10)
            );
        }


        //删除某缓存
        public void evict(Long id){
            redisTemplate.delete(CacheKey.model(id));
        }

}