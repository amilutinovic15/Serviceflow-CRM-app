DROP TABLE IF EXISTS webhook_subscriptions;

CREATE TABLE service_job_status_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    service_job_id BIGINT NOT NULL,
    previous_status VARCHAR(40),
    new_status VARCHAR(40) NOT NULL,
    changed_by_user_id BIGINT,
    changed_by_email VARCHAR(160),
    note TEXT,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_service_job_status_history_job
        FOREIGN KEY (service_job_id) REFERENCES service_jobs (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_service_job_status_history_user
        FOREIGN KEY (changed_by_user_id) REFERENCES app_users (id),
    INDEX idx_service_job_status_history_job_id (service_job_id),
    INDEX idx_service_job_status_history_changed_at (changed_at)
);
