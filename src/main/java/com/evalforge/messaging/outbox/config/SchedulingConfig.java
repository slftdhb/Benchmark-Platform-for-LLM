package com.evalforge.messaging.outbox.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "evalforge.outbox.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {
    
}
