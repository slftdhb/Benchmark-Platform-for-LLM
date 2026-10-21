package com.evalforge.cache;

public final class CacheKey {

        private CacheKey(){};
        
        public static String model(Long id){
            return "cache:model:"+id;
        }
}
