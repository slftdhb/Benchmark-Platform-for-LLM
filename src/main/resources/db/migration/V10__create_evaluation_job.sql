CREATE TABLE evaluation_job(
            id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            job_name VARCHAR(256) NULL DEFAULT 'evaluation_job',
            owner_id BIGINT NOT NULL,   
            model_endpoint_id BIGINT NOT NULL,
            benchmark_version_id BIGINT NOT NULL,
            request_id VARCHAR(128) NOT NULL,
            status VARCHAR(24) NOT NULL DEFAULT 'PENDING',

            total INT NOT NULL DEFAULT 0,
            succeed INT NOT NULL DEFAULT 0,
            failed INT NOT NULL DEFAULT 0,

            config_json JSON NULL,
            config_hash char(64) NULL,


            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            finished_at TIMESTAMP NULL,
            started_at TIMESTAMP NULL,

            UNIQUE KEY uk_eval_owner_request(owner_id, request_id),
            KEY idx_eval_owner_created(owner_id,created_at,id),
            KEY idx_eval_status_created(status,created_at),

            CONSTRAINT fk_eval_owner FOREIGN KEY (owner_id) REFERENCES app_user(id),
            CONSTRAINT fk_eval_model_endpoint FOREIGN KEY (model_endpoint_id) REFERENCES model_endpoint(id),
            CONSTRAINT fk_eval_benchmark_version FOREIGN KEY (benchmark_version_id) REFERENCES benchmark_version(id)
);


CREATE TABLE evaluation_task(
            id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
            job_id BIGINT NOT NULL,
            batch_no INT NOT NULL,
            status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
            attempt INT NOT NULL DEFAULT 0,
            
            worker_id BIGINT NULL,
            error_code VARCHAR(64) NULL,

            start_sample_id BIGINT NULL,
            end_sample_id BIGINT NULL,

            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            started_at TIMESTAMP NULL,
            finished_at TIMESTAMP NULL,

            UNIQUE KEY uk_eval_task_(job_id,batch_no),
            KEY idx_eval_task_job_status(job_id,status),

            CONSTRAINT fk_eval_task_job FOREIGN KEY (job_id) REFERENCES evaluation_job(id)
);  