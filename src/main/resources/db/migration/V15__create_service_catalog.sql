CREATE TABLE service_catalog_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(140) NOT NULL,
    category VARCHAR(100),
    description TEXT,
    default_price DECIMAL(10, 2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_service_catalog_items_active (active),
    INDEX idx_service_catalog_items_category (category)
);

ALTER TABLE service_jobs
    ADD COLUMN catalog_item_id BIGINT,
    ADD CONSTRAINT fk_service_jobs_catalog_item
        FOREIGN KEY (catalog_item_id) REFERENCES service_catalog_items (id),
    ADD INDEX idx_service_jobs_catalog_item_id (catalog_item_id);
