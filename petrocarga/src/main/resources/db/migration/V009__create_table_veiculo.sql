CREATE TABLE veiculo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    placa VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    tipo VARCHAR(50),
    cor VARCHAR(30),
    comprimento NUMERIC(4, 2),
    usuario_id UUID NOT NULL,
    CONSTRAINT fk_veiculo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);