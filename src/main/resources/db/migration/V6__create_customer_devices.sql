CREATE TABLE customer_devices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    brand VARCHAR(80),
    model VARCHAR(80),
    serial_number VARCHAR(120),
    location VARCHAR(160),
    installed_at DATE,
    last_service_at DATE,
    next_service_due_at DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_customer_devices_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id),
    INDEX idx_customer_devices_customer_id (customer_id),
    INDEX idx_customer_devices_next_service_due_at (next_service_due_at)
);
