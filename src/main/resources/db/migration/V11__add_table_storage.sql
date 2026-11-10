CREATE TABLE stored_object(
        id BIGINT PRIMARY KEY AUTO_INCREMENT,

        owner_id BIGINT NOT NULL,

        bucket_name VARCHAR(128) NOT NULL,
        object_key VARCHAR(512) NOT NULL,

        object_type VARCHAR(48) NOT NULL,
        related_resource_id BIGINT NOT NULL,

        original_filename VARCHAR(255) NULL,
        content_type VARCHAR(128) NOT NULL,
        size_bytes BIGINT NOT NULL,
        sha256 CHAR(64)  NULL,

        status VARCHAR(24) NOT NULL DEFAULT 'PENDING',

        error_message VARCHAR(500) NULL,

        created_at TIMESTAMP NOT NULL
            DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP NOT NULL
            DEFAULT CURRENT_TIMESTAMP
            ON UPDATE CURRENT_TIMESTAMP,

        UNIQUE KEY uk_stored_object_location (
            bucket_name,
            object_key
        ),

        KEY idx_stored_object_resource (
            owner_id,
            object_type,
            related_resource_id,
            status
        ),

        CONSTRAINT fk_stored_object_owner
            FOREIGN KEY (owner_id)
            REFERENCES app_user(id)
);