CREATE TABLE outbox_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    event_id CHAR(36) NOT NULL,
    event_type VARCHAR(64) NOT NULL,

    aggregate_type VARCHAR(32) NOT NULL,
    aggregate_id BIGINT NOT NULL,

    topic_name VARCHAR(128) NOT NULL,
    message_key VARCHAR(128) NOT NULL,
    payload_json JSON NOT NULL,

    status VARCHAR(24) NOT NULL DEFAULT 'PENDING',

    attempts INT NOT NULL DEFAULT 0,
    available_at DATETIME(3) NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3),

    locked_by VARCHAR(64) NULL,
    locked_until DATETIME(3) NULL,

    published_at DATETIME(3) NULL,
    last_error VARCHAR(500) NULL,

    created_at DATETIME(3) NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3),

    updated_at DATETIME(3) NOT NULL
        DEFAULT CURRENT_TIMESTAMP(3)
        ON UPDATE CURRENT_TIMESTAMP(3),

    UNIQUE KEY uk_outbox_event_id (event_id),

    UNIQUE KEY uk_outbox_initial_event (
        aggregate_type,
        aggregate_id,
        event_type
    ),

    KEY idx_outbox_pending (
        status,
        available_at,
        id
    ),

    KEY idx_outbox_lease (
        status,
        locked_until
    )
);