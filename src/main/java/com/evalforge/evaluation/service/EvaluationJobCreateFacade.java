package com.evalforge.evaluation.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.common.hasher.EvaluationJobCreateRequestHasher;
import com.evalforge.evaluation.dto.CreateEvaluationJobRequest;
import com.evalforge.idempotency.IdempotencyCacheEntry;
import com.evalforge.idempotency.IdempotencyService;
import com.evalforge.idempotency.RedisIdempotencyCache;

import tools.jackson.core.JacksonException;

@Service 
public class EvaluationJobCreateFacade {
        private final RedisIdempotencyCache redis;
        private final IdempotencyService idempotencyService;
        private final EvaluationJobCreateRequestHasher hasher;

        private static final Logger log = LoggerFactory.getLogger(EvaluationJobCreateFacade.class);

        public EvaluationJobCreateFacade(RedisIdempotencyCache redis,IdempotencyService idempotencyService,EvaluationJobCreateRequestHasher hasher){
                this.redis = redis;
                this.idempotencyService= idempotencyService;
                this.hasher=hasher;
        }

       
        public Long createEvaluationJobWithCache(Long userId,CreateEvaluationJobRequest request, String idempotencyKey){
                    String hash = hasher.calculateRequestHash(request);
                    IdempotencyCacheEntry cached = null;
                    //查缓存
                    try{
                        cached = redis.getJob(userId, idempotencyKey);
                    }catch(DataAccessException | JacksonException e){
                        log.warn("failed to read idempotency key ,falling back to mysql");
                    }

                    if(cached != null){
                        if(!hash.equals(cached.requestHash())){
                            throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_CONFLICT, "idempotencyKey has already existed with different parameteres");
                        }
                        return cached.resourceId();
                    }

                    Long resourceId = idempotencyService.createEvaluationJob(userId, request, idempotencyKey);
                    //此时mysql事务已经提交,不应该因为redis的失败而报错整个事务的失败

                    try{
                            redis.putJob(userId, idempotencyKey, new IdempotencyCacheEntry(hash,resourceId));
                    }catch(DataAccessException | JacksonException e){
                            log.warn("failed to write idempotency key to redis cache,ressourceId = {}",resourceId,e);
                    }
                    
                    return resourceId;
        } 
}
