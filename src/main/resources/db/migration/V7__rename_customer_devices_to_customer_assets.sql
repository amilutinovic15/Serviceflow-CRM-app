RENAME TABLE customer_devices TO customer_assets;

ALTER TABLE customer_assets
    RENAME COLUMN brand TO category,
    RENAME COLUMN model TO asset_identifier,
    RENAME COLUMN serial_number TO external_reference,
    RENAME COLUMN installed_at TO service_started_at;
