CREATE TABLE IF NOT EXISTS notificacao (
    id UUID primary key,
    usuario_id UUID NOT NULL REFERENCES usuario(id),
    titulo VARCHAR(120) NOT NULL,
    mensagem TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT FALSE,
    criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB DEFAULT NULL
);

CREATE INDEX IF NOT EXISTS idx_notificacao_usuario
ON notificacao (usuario_id);

CREATE INDEX IF NOT EXISTS idx_notificacao_usuario_lida
ON notificacao (usuario_id, lida);
