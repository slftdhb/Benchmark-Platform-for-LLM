ALTER TABLE idempotency_record
    ADD COLUMN user_id BIGINT  NOT NULL;

ALTER TABLE idempotency_record 
    DROP INDEX uk_idempotency_operation_key,
    ADD UNIQUE KEY uk_idempotency_operation_key (user_id, operation, idempotency_key);