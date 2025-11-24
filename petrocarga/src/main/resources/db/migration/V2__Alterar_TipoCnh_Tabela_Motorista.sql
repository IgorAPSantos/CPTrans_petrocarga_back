ALTER TABLE motorista 
ALTER COLUMN tipo_cnh TYPE VARCHAR(2);

UPDATE motorista
SET tipo_cnh = 'AB'
WHERE tipo_cnh = 'A';