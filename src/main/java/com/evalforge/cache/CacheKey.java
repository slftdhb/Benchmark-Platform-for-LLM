package com.evalforge.cache;

public final class CacheKey {

        private CacheKey(){};
        
        public static String model(Long id){
            return "cache:model:"+id;
        }

        public static String benchmark_create_idempotency_key(String idempotency_key,Long userId){
            return "idem:benchmark-create:"+userId+":"+idempotency_key;
        }

        public static String modelRateLimit(Long modelId){
            return "rate:model:"+modelId;
        }

        public static String evaluationJobCreate(Long userId,String idempotencyKey){
            return "idem:evaluation-job-create:"+userId+":"+idempotencyKey;
        }
}
