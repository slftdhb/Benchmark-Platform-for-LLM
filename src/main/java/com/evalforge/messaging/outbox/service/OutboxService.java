package com.evalforge.messaging.outbox.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import com.evalforge.messaging.dto.TaskDispatchMessage;
import com.evalforge.messaging.outbox.entity.OutboxEvent;
import com.evalforge.messaging.outbox.enums.OutboxStatus;
import com.evalforge.messaging.outbox.mapper.OutboxMapper;

@Service 
public class OutboxService {
            private final OutboxMapper outboxMapper;
            private final ObjectMapper objectMapper;

            @Value("${evalforge.kafka.task-topic}")
            private String taskTopic;

            public OutboxService(OutboxMapper outboxMapper, ObjectMapper objectMapper) {
                    this.outboxMapper = outboxMapper;
                    this.objectMapper = objectMapper;
            }


            public int insertPending(TaskDispatchMessage message) {

                        OutboxEvent event = new OutboxEvent();
                        event.setEventId(message.eventId());
                        event.setEventType("TASK_DISPATCH");
                        event.setAggregateId(message.taskId());
                        event.setAggregateType("EVALUATION");
                        event.setTopicName(taskTopic);
                        //用jobid来做key使同一job的task消息可以放在同一partition里
                        event.setMessageKey(String.valueOf(message.jobId()));

                        try{
                            String payloadJson = objectMapper.writeValueAsString(message);
                            event.setPayloadJson(payloadJson);
                        } catch (JacksonException e) {
                            throw new IllegalStateException("Failed to serialize task dispatch message", e);
                        }

                        return outboxMapper.insertPending(event);
            }   

}
