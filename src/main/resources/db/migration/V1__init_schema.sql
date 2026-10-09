CREATE TABLE model_endpoint(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    base_url VARCHAR(512) NOT NULL,
    model_name VARCHAR(256) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    UNIQUE KEY uk_model_endpoint_name (name)
 );

 CREATE TABLE benchmark(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(1024),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY(id),
    UNIQUE KEY uk_benchmark_name(name)
 );

 CREATE TABLE benchmark_version(
    id BIGINT NOT NULL AUTO_INCREMENT,
    benchmark_id BIGINT NOT NULL,
    version_no INT NOT NULL,
    content_hash VARCHAR(128),
    sample_count INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    create_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    UNIQUE KEY uk_benchmark_version(
        benchmark_id,
        version_no
    ),

    KEY idx_benchmark_version_benchmark_id(benchmark_id),
    CONSTRAINT fk_benchmark_version_benchmark FOREIGN KEY(benchmark_id) REFERENCES benchmark(id)
 );