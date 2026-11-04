ALTER TABLE service_jobs
    ADD COLUMN assignee_user_id BIGINT,
    ADD CONSTRAINT fk_service_jobs_assignee
        FOREIGN KEY (assignee_user_id) REFERENCES app_users (id),
    ADD INDEX idx_service_jobs_assignee_user_id (assignee_user_id);
