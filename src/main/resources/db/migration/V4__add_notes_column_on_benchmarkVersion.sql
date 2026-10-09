ALTER TABLE benchmark_version
ADD COLUMN notes varchar(1024) NULL,
ADD COLUMN published_at DATETIME NULL;
