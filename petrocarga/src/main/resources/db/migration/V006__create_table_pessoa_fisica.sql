CREATE TABLE pessoa_fisica (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cpf VARCHAR(14) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    usuario_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_pessoa_fisica_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);