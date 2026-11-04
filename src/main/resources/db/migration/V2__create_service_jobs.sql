CREATE TABLE service_jobs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    title VARCHAR(140) NOT NULL,
    description TEXT,
    status VARCHAR(40) NOT NULL,
    scheduled_at DATETIME(6),
    completed_at DATETIME(6),
    price DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_service_jobs_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id),
    INDEX idx_service_jobs_customer_id (customer_id),
    INDEX idx_service_jobs_status (status),
    INDEX idx_service_jobs_scheduled_at (scheduled_at)
);
