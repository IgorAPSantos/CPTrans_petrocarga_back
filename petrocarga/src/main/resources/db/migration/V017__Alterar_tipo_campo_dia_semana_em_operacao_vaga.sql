ALTER TABLE operacao_vaga
    ALTER COLUMN dia_semana TYPE VARCHAR(20)
    USING dia_semana::text;

DROP TYPE IF EXISTS dia_da_semana;
