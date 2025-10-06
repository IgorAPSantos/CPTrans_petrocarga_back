CREATE TABLE motorista (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cnh VARCHAR(20) NOT NULL UNIQUE,
    usuario_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_motorista_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);