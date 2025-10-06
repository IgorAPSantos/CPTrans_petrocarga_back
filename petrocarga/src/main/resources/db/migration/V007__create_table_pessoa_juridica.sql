CREATE TABLE pessoa_juridica (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    razao_social VARCHAR(255),
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    nome_fantasia VARCHAR(255),
    telefone VARCHAR(20),
    usuario_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_pessoa_juridica_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);