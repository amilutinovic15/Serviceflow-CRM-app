CREATE TABLE service_job_comments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_job_id BIGINT NOT NULL,
    author_user_id BIGINT,
    author_email VARCHAR(160),
    body TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_service_job_comments_job
        FOREIGN KEY (service_job_id) REFERENCES service_jobs (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_service_job_comments_author
        FOREIGN KEY (author_user_id) REFERENCES app_users (id),
    INDEX idx_service_job_comments_job_id (service_job_id),
    INDEX idx_service_job_comments_created_at (created_at)
);
