package com.evalforge.messaging.outbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.evalforge.messaging.dto.TaskDispatchMessage;
import com.evalforge.messaging.outbox.service.OutboxPublisher;
import com.evalforge.messaging.outbox.service.OutboxService;

@SpringBootTest(properties = {
        "evalforge.outbox.initial-delay-ms=3600000",
        "evalforge.outbox.publish-interval-ms=3600000",
        "evalforge.outbox.max-attempts=3",
        "spring.kafka.listener.auto-startup=false"
})
@Transactional
class OutboxPublisherTest {

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OutboxPublisher outboxPublisher;

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void insertPendingWritesPendingRow() {
        TaskDispatchMessage message = insertMessage();

        Map<String, Object> row = rowOf(message.eventId());

        assertEquals("PENDING", row.get("status"));
        assertEquals("evaluation.task.dispatch.v1", row.get("topic_name"));
        assertEquals(String.valueOf(message.jobId()), row.get("message_key"));
        assertTrue(row.get("payload_json").toString().contains(message.eventId()));
    }

    @Test
    void sendSuccessMarksPublished() {
        CompletableFuture<SendResult<String, String>> success =
                CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(success);

        TaskDispatchMessage message = insertMessage();
        outboxPublisher.publishPending();

        Map<String, Object> row = rowOf(message.eventId());

        assertEquals("PUBLISHED", row.get("status"));
        assertEquals(1, ((Number) row.get("attempts")).intValue());
        assertNotNull(row.get("published_at"));
    }

    @Test
    void sendFailureKeepsPendingAndBacksOff() {
        CompletableFuture<SendResult<String, String>> failure =
                CompletableFuture.failedFuture(new RuntimeException("kafka down"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(failure);

        TaskDispatchMessage message = insertMessage();
        outboxPublisher.publishPending();

        Map<String, Object> row = rowOf(message.eventId());

        assertEquals("PENDING", row.get("status"));
        assertEquals(1, ((Number) row.get("attempts")).intValue());
        assertNotNull(row.get("last_error"));
        assertTrue(row.get("last_error").toString().contains("kafka down"));
        assertTrue(((LocalDateTime) row.get("available_at"))
                .isAfter(LocalDateTime.now()));
    }

    @Test
    void reachingMaxAttemptsMarksDead() {
        CompletableFuture<SendResult<String, String>> failure =
                CompletableFuture.failedFuture(new RuntimeException("still down"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(failure);

        TaskDispatchMessage message = insertMessage();
        Long id = idOf(message.eventId());

        // 模拟已经失败 2 次，本次 claim 后 attempts 变成 3
        jdbc.update(
                "UPDATE outbox_event " +
                "SET attempts = 2, available_at = DATE_SUB(NOW(3), INTERVAL 1 SECOND) " +
                "WHERE id = ?", id);

        outboxPublisher.publishPending();

        Map<String, Object> row = rowOf(message.eventId());
        assertEquals("DEAD", row.get("status"));
        assertEquals(3, ((Number) row.get("attempts")).intValue());
    }

    private TaskDispatchMessage insertMessage() {
        long jobId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        long taskId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);

        TaskDispatchMessage message = new TaskDispatchMessage(
                1,
                UUID.randomUUID().toString(),
                jobId,
                taskId,
                Instant.now());

        assertEquals(1, outboxService.insertPending(message));
        return message;
    }

    private Long idOf(String eventId) {
        return jdbc.queryForObject(
                "SELECT id FROM outbox_event WHERE event_id = ?",
                Long.class, eventId);
    }

    private Map<String, Object> rowOf(String eventId) {
        return jdbc.queryForMap(
                "SELECT status, attempts, topic_name, message_key, " +
                "       payload_json, available_at, published_at, last_error " +
                "FROM outbox_event WHERE event_id = ?",
                eventId);
    }
}