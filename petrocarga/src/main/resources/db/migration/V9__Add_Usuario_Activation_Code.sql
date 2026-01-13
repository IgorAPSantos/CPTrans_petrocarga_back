-- Add fields for account activation via verification code (OTP)
-- Adds a 6-digit verification code and its expiration timestamp
-- Sets default of `ativo` to false for new records

ALTER TABLE usuario
    ADD COLUMN verification_code VARCHAR(6);

ALTER TABLE usuario
    ADD COLUMN verification_code_expires_at TIMESTAMP;

-- Ensure future created rows default to inactive
ALTER TABLE usuario
    ALTER COLUMN ativo SET DEFAULT false;

-- Optional: do not change existing active users; uncomment to set NULLs to false
-- UPDATE usuario SET ativo = false WHERE ativo IS NULL;
