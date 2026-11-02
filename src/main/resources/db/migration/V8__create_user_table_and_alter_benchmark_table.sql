CREATE TABLE  app_user(
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(64) NOT NULL,
                password_hash VARCHAR(256) NOT NULL,
                role VARCHAR(32) NOT NULL DEFAULT 'USER',
                enabled boolean NOT NULL DEFAULT TRUE,
                created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                UNIQUE KEY uk_app_user_username(username)
);

ALTER TABLE benchmark
    ADD COLUMN owner_id BIGINT NULL;

ALTER TABLE benchmark
    ADD CONSTRAINT fk_benchmark_owner FOREIGN KEY (owner_id) REFERENCES app_user(id);

CREATE INDEX idx_benchmark_owner ON benchmark(owner_id, created_at DESC , id DESC);