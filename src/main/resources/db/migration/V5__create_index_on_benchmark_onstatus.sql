CREATE INDEX idx_benchmark_status_created
ON benchmark(status, created_at DESC, id DESC);