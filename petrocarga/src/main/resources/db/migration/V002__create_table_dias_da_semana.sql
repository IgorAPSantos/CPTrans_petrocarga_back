CREATE TABLE dias_da_semana (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dia VARCHAR(50) NOT NULL UNIQUE
);