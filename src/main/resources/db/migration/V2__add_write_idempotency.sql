ALTER TABLE expenses
    ADD COLUMN idempotency_key VARCHAR(100) NULL,
    ADD COLUMN request_hash CHAR(64) NULL,
    ADD CONSTRAINT uk_expenses_idempotency UNIQUE (paid_by, idempotency_key);

ALTER TABLE settlements
    ADD COLUMN idempotency_key VARCHAR(100) NULL,
    ADD COLUMN request_hash CHAR(64) NULL,
    ADD CONSTRAINT uk_settlements_idempotency UNIQUE (payer_id, idempotency_key);
