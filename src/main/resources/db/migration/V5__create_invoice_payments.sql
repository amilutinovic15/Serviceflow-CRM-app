CREATE TABLE invoice_payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    paid_at DATE NOT NULL,
    method VARCHAR(40) NOT NULL,
    reference VARCHAR(120),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_invoice_payments_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoices (id)
        ON DELETE CASCADE,
    INDEX idx_invoice_payments_invoice_id (invoice_id),
    INDEX idx_invoice_payments_paid_at (paid_at)
);
