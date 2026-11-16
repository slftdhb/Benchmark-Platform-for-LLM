package com.evalforge.messaging.outbox.entity;

import java.time.LocalDateTime;

public class OutboxEvent {
        Long id;
        String eventId;
        String eventType;
        String aggregateType;
        Long aggregateId;
        String topicName;
        String messageKey;
        String payloadJson;
        String status;
        Integer attempts;
        LocalDateTime availableAt;
        String lockedBy;
        LocalDateTime lockedUntil;
        LocalDateTime publishedAt;
        String lastError;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;

        
        public Long getId() {
                return id;
        }
        public void setId(Long id) {
                this.id = id;
        }
        public String getEventId() {
                return eventId;
        }
        public void setEventId(String eventId) {
                this.eventId = eventId;
        }
        public String getEventType() {
                return eventType;
        }
        public void setEventType(String eventType) {
                this.eventType = eventType;
        }
        public String getAggregateType() {
                return aggregateType;
        }
        public void setAggregateType(String aggregateType) {
                this.aggregateType = aggregateType;
        }
        public Long getAggregateId() {
                return aggregateId;
        }
        public void setAggregateId(Long aggregateId) {
                this.aggregateId = aggregateId;
        }
        public String getTopicName() {
                return topicName;
        }
        public void setTopicName(String topicName) {
                this.topicName = topicName;
        }
        public String getMessageKey() {
                return messageKey;
        }
        public void setMessageKey(String messageKey) {
                this.messageKey = messageKey;
        }
        public String getPayloadJson() {
                return payloadJson;
        }
        public void setPayloadJson(String payloadJson) {
                this.payloadJson = payloadJson;
        }
        public String getStatus() {
                return status;
        }
        public void setStatus(String status) {
                this.status = status;
        }
        public Integer getAttempts() {
                return attempts;
        }
        public void setAttempts(Integer attempts) {
                this.attempts = attempts;
        }
        public LocalDateTime getAvailableAt() {
                return availableAt;
        }
        public void setAvailableAt(LocalDateTime availableAt) {
                this.availableAt = availableAt;
        }
        public String getLockedBy() {
                return lockedBy;
        }
        public void setLockedBy(String lockedBy) {
                this.lockedBy = lockedBy;
        }
        public LocalDateTime getLockedUntil() {
                return lockedUntil;
        }
        public void setLockedUntil(LocalDateTime lockedUntil) {
                this.lockedUntil = lockedUntil;
        }
        public LocalDateTime getPublishedAt() {
                return publishedAt;
        }
        public void setPublishedAt(LocalDateTime publishedAt) {
                this.publishedAt = publishedAt;
        }
        public String getLastError() {
                return lastError;
        }
        public void setLastError(String lastError) {
                this.lastError = lastError;
        }
        public LocalDateTime getCreatedAt() {
                return createdAt;
        }
        public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
        }
        public LocalDateTime getUpdatedAt() {
                return updatedAt;
        }
        public void setUpdatedAt(LocalDateTime updatedAt) {
                this.updatedAt = updatedAt;
        }
}
