CREATE TABLE business_profiles (
    id BIGINT NOT NULL,
    business_name VARCHAR(160) NOT NULL,
    legal_name VARCHAR(160),
    tax_number VARCHAR(80),
    registration_number VARCHAR(80),
    email VARCHAR(160),
    phone VARCHAR(40),
    website VARCHAR(160),
    address VARCHAR(255),
    city VARCHAR(120),
    country VARCHAR(120),
    currency VARCHAR(3) NOT NULL,
    invoice_footer TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

INSERT INTO business_profiles (
    id,
    business_name,
    legal_name,
    email,
    phone,
    address,
    city,
    country,
    currency,
    invoice_footer
) VALUES (
    1,
    'ServiceFlow Demo Business',
    'ServiceFlow Demo Business',
    'office@serviceflow.local',
    '+381641234567',
    'Bulevar Oslobodjenja 10',
    'Novi Sad',
    'Serbia',
    'RSD',
    'Thank you for your business.'
);
