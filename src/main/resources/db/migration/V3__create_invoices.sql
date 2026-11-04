CREATE TABLE invoices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    invoice_number VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,
    issued_at DATE NOT NULL,
    due_at DATE,
    subtotal DECIMAL(10, 2) NOT NULL,
    tax_rate DECIMAL(5, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_invoices_invoice_number UNIQUE (invoice_number),
    CONSTRAINT fk_invoices_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id),
    INDEX idx_invoices_customer_id (customer_id),
    INDEX idx_invoices_status (status),
    INDEX idx_invoices_issued_at (issued_at)
);

CREATE TABLE invoice_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    service_job_id BIGINT,
    description VARCHAR(255) NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    line_total DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_invoice_items_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoices (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_invoice_items_service_job
        FOREIGN KEY (service_job_id) REFERENCES service_jobs (id),
    INDEX idx_invoice_items_invoice_id (invoice_id),
    INDEX idx_invoice_items_service_job_id (service_job_id)
);
