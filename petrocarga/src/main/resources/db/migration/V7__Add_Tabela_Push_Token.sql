CREATE TABLE IF NOT EXISTS push_token (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuario(id),
    token TEXT NOT NULL,
    plataforma VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT now(), 
    CONSTRAINT uq_push_token_usuario
    UNIQUE (token, usuario_id)
);

CREATE INDEX IF NOT EXISTS idx_push_token_usuario_ativo
ON push_token (usuario_id, ativo);
