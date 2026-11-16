package com.evalforge.messaging.outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.evalforge.messaging.dto.TaskDispatchMessage;
import com.evalforge.messaging.outbox.service.OutboxPublisher;
import com.evalforge.messaging.outbox.service.OutboxService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
        "evalforge.outbox.initial-delay-ms=3600000",
        "evalforge.outbox.publish-interval-ms=3600000",
        "spring.kafka.listener.auto-startup=false"
})
@Transactional
class OutboxKafkaIntegrationTest {

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private OutboxPublisher outboxPublisher;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${evalforge.kafka.task-topic}")
    private String topic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Test
    void outboxEventIsPublishedToKafka() {
        long jobId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        long taskId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);

        TaskDispatchMessage message = new TaskDispatchMessage(
                1,
                UUID.randomUUID().toString(),
                jobId,
                taskId,
                Instant.now());

        assertEquals(1, outboxService.insertPending(message));

        // 手动触发一次 Publisher，避免依赖 @Scheduled 的执行时机
        outboxPublisher.publishPending();

        String status = jdbc.queryForObject(
                "SELECT status FROM outbox_event WHERE event_id = ?",
                String.class, message.eventId());
        assertEquals("PUBLISHED", status);

        ConsumerRecord<String, String> record = findRecord(message.eventId());

        assertNotNull(record, "Kafka 中没有找到对应的 outbox 消息");
        assertEquals(String.valueOf(jobId), record.key());

        JsonNode payload = objectMapper.readTree(record.value());

        assertEquals(message.eventId(), payload.get("eventId").asText());
        assertEquals(jobId, payload.get("jobId").asLong());
assertEquals(taskId, payload.get("taskId").asLong());
    }

    private ConsumerRecord<String, String> findRecord(String eventId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                "outbox-it-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, String> consumer =
                     new KafkaConsumer<>(props)) {

            consumer.subscribe(List.of(topic));

            long deadline = System.currentTimeMillis() + 15_000;

            while (System.currentTimeMillis() < deadline) {
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, String> record : records) {
                    if (record.value() != null
                            && record.value().contains(eventId)) {
                        return record;
                    }
                }
            }
            return null;
        }
    }
}