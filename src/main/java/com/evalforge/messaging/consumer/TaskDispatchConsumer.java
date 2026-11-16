package com.evalforge.messaging.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component 
public class TaskDispatchConsumer {

    private static final Logger log = LoggerFactory.getLogger(TaskDispatchConsumer.class);
    
    @KafkaListener(
        topics = "${evalforge.kafka.task-topic}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String,String> record){
            log.info(
                "Kafka message received: key={}, value={}, partition={}, offset={}",
                record.key(),
                record.value(),
                record.partition(),
                record.offset()
            );
    }
    
}
