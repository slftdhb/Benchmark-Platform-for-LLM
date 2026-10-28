package com.evalforge.benchmark.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.evalforge.benchmark.dto.CreateBenchmarkRequest;
import com.evalforge.cache.CacheKey;
import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.common.hasher.BenchmarkCreateRequestHasher;
import com.evalforge.idempotency.IdempotencyCacheEntry;
import com.evalforge.idempotency.IdempotencyService;
import com.evalforge.idempotency.RedisIdempotencyCache;

import tools.jackson.core.JacksonException;

@Service
public class BenchmarkCreateFacade {
        private final RedisIdempotencyCache redis;
        private final IdempotencyService idempotencyService;
        private final BenchmarkCreateRequestHasher hasher;

        private static final Logger log = LoggerFactory.getLogger(BenchmarkCreateFacade.class);

        public BenchmarkCreateFacade(RedisIdempotencyCache redis,IdempotencyService idempotencyService,BenchmarkCreateRequestHasher hasher){
            this.redis=redis;
            this.idempotencyService=idempotencyService;
            this.hasher=hasher;
        }

        //先检查缓存
        public Long crateBenchmarkWithRedisCache(CreateBenchmarkRequest request ,String idempotencyKey){  
            
            String hash =  hasher.calculateRequestHash(request);
            
            IdempotencyCacheEntry cached = null;

            try{
                 cached = redis.get(idempotencyKey);
            }catch(DataAccessException | JacksonException ex){
                    log.warn("Failed to read idempotency cache ,falling back to mysql ",ex);
            }
            
            //有对应的缓存结果,且内容一致
            if(cached != null ){
                    if(!(hash.equals(cached.requestHash()))){
                        throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_CONFLICT, "Idempotency-Key was already used with different parameters");
                    }
                //由对应的key而且内容一致，返回已经存在的资源ID
                return cached.resourceId();
            }

            //未命中缓存，就进入mysql的事务,mysql事务结束后写入缓存
            Long id =idempotencyService.createBenchmark(request, idempotencyKey);

            //mysql完成提交后，redis失败不应该反应为创建事务失败
            try{
                    redis.put(idempotencyKey,new IdempotencyCacheEntry(hash,id));
            }catch(DataAccessException  | JacksonException ex){
                    log.warn("fialed to populate idempotency chache ,resource = {}",id, ex);
            }
            
            return id;
        }
}
