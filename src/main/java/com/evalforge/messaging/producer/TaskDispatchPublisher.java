package com.evalforge.messaging.producer;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.evalforge.messaging.dto.TaskDispatchMessage;

import tools.jackson.databind.ObjectMapper;

@Service 
public class TaskDispatchPublisher {
            private final KafkaTemplate<String, String> kafka;
            private final ObjectMapper objectMapper;

            @Value("${evalforge.kafka.task-topic}")
            private String topic;

            public TaskDispatchPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
                this.kafka = kafkaTemplate;
                this.objectMapper = objectMapper;
            }

            public CompletableFuture<SendResult<String,String>> publishTask(TaskDispatchMessage message) {

                String key = message.jobId().toString();
                String json = objectMapper.writeValueAsString(message);

                return kafka.send(topic,key,json);
            }
}
