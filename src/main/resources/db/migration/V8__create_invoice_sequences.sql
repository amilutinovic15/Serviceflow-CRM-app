CREATE TABLE invoice_sequences (
    invoice_year INT NOT NULL,
    last_number INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (invoice_year)
);
