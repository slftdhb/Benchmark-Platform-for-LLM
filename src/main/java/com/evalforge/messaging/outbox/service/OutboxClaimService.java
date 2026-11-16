package com.evalforge.messaging.outbox.service;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evalforge.messaging.outbox.entity.OutboxEvent;
import com.evalforge.messaging.outbox.mapper.OutboxMapper;

@Service
public class OutboxClaimService {
            private final OutboxMapper outboxMapper;

            private final String instanceId ;

            @Value("${evalforge.outbox.lease-seconds:30}")
            private int leaseSeconds;

            public OutboxClaimService(OutboxMapper outboxMapper) {
                    this.outboxMapper = outboxMapper;
                    this.instanceId = ManagementFactory.getRuntimeMXBean().getName();
            }
            
            @Transactional 
            public List<OutboxEvent> claimBatch(int limit) {
                    List<OutboxEvent> Events = outboxMapper.findDue(limit);
                    List<OutboxEvent> claimedEvents =new ArrayList<>();
                    
                    //只返回被实例成功claim的消息
                    for(OutboxEvent event : Events) {
                        LocalDateTime lockedUntil = LocalDateTime.now().plusSeconds(leaseSeconds);
                        int claimed = outboxMapper.claim(event.getId(), instanceId, lockedUntil);
                        if(claimed == 1){
                            claimedEvents.add(event);
                        }
                    }
                    return claimedEvents;
            }
}
