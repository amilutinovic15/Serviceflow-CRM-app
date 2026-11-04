ALTER TABLE service_jobs
    ADD COLUMN priority VARCHAR(40) NOT NULL DEFAULT 'NORMAL',
    ADD INDEX idx_service_jobs_priority (priority);
