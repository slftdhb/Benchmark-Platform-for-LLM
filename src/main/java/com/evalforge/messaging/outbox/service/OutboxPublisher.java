package com.evalforge.messaging.outbox.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.evalforge.messaging.outbox.entity.OutboxEvent;
import com.evalforge.messaging.outbox.mapper.OutboxMapper;

@Component 
public class OutboxPublisher {  
            private final OutboxClaimService ClaimService;
            private final OutboxMapper outboxMapper;
            private final KafkaTemplate<String, String > kafka;

            private final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);


            @Value("${evalforge.outbox.publish-batch-size:10}")
            private int batchSize;

            @Value ("${evalforge.outbox.send-timeout-seconds:5}")
            private int sendTimeoutSeconds;

            @Value ("${evalforge.outbox.max-attempts:5}")
            private int maxAttempts;

            @Value ("${evalforge.outbox.backoff-base-seconds:5}")
            private int backoffBaseSeconds;

            @Value ("${evalforge.outbox.backoff-max-seconds:300}")
            private int backoffMaxSeconds;

            public OutboxPublisher(OutboxClaimService ClaimService, OutboxMapper outboxMapper, KafkaTemplate<String, String > kafka) {
                    this.ClaimService = ClaimService;
                    this.outboxMapper = outboxMapper;
                    this.kafka = kafka;
            }

            @Scheduled (initialDelayString = "${evalforge.outbox.initial-delay-ms:0}",
                        fixedDelayString = "${evalforge.outbox.publish-interval-ms:2000}")
            public void publishPending(){
                    List<OutboxEvent> events = ClaimService.claimBatch(batchSize);
                    for(OutboxEvent event : events) {
                        publishOne(event);
                    }
            }

            //发送成功后再标记已经发布
            private void publishOne(OutboxEvent event) {
                    try{
                        kafka.send(event.getTopicName(), event.getMessageKey(), event.getPayloadJson()).get(sendTimeoutSeconds,TimeUnit.SECONDS);
                        outboxMapper.markPublished(event.getId(), LocalDateTime.now());

                    }catch(InterruptedException e){
                        Thread.currentThread().interrupt();
                        markFailure(event,e);
                        return ;
                    }catch(Exception e){
                        markFailure(event,e);
                        return ;
                    }
            }

            private void markFailure(OutboxEvent event, Exception e) {
                    int attempts = (event.getAttempts() == null ? 0 : event.getAttempts()) + 1;
                    int backoff=Math.min(   
                                backoffBaseSeconds * (1 << Math.min(attempts-1, 10)),
                                backoffMaxSeconds
                    );  
                    LocalDateTime availableAt = LocalDateTime.now().plusSeconds(backoff);
                    String error  = truncateErrorMessage(e.getMessage(),500);

                    try{
                        outboxMapper.markFailure(event.getId(), error, availableAt, maxAttempts);
                    }catch(Exception ex){
                        log.error("\n\nFailed to mark failure for outbox event {}: {}", event.getId(), ex.getMessage());
                    }

                    log.warn("\n\nFailed to publish outbox event {}: {}. Attempts: {}. Next attempt at: {}", event.getId(), error, attempts, availableAt);
                    
            }

            private String truncateErrorMessage(String message, int maxLength) {
                    if(message == null) return "unkown";
                    return message.length() <= maxLength ? message : message.substring(0, maxLength);
            }
}
