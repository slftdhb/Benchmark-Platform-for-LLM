CREATE TABLE benchmark_sample(
    id BIGINT NOT NULL AUTO_INCREMENT,
    version_id BIGINT NOT NULL,
    sample_key VARCHAR(128) NOT NULL,
    input_json JSON NOT NULL,
    gold_json JSON NULL,
    metadata_json JSON NULL,
    line_no INT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_version_sample(
        version_id,
        sample_key
    ),
    KEY idx_sample_version(
        version_id,
        id
    ),
    CONSTRAINT fk_sample_version FOREIGN KEY (version_id) REFERENCES benchmark_version(id)

    
);