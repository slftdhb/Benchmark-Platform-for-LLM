package com.evalforge.cache;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.evalforge.event.ModelChangedEvent;

@Component
public class ModelCacheValidationListener {
            private final ModelCacheService cacheService;

            public ModelCacheValidationListener(ModelCacheService cacheService){
                this.cacheService=cacheService;
            }

            @TransactionalEventListener (phase = TransactionPhase.AFTER_COMMIT)
            public void onChanged(ModelChangedEvent event){
                cacheService.evict(event.modelId());
            }
            
}
