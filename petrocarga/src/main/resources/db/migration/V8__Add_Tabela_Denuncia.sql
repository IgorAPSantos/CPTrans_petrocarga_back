CREATE TABLE IF NOT EXISTS denuncia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    descricao TEXT NOT NULL,
    criado_por UUID NOT NULL REFERENCES usuario(id),
    vaga_id UUID NOT NULL REFERENCES vaga(id),
    reserva_id UUID NOT NULL REFERENCES reserva(id),
    status VARCHAR(50) NOT NULL DEFAULT 'ABERTA',
    tipo VARCHAR(50) NOT NULL,
    resposta TEXT,
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL,
    atualizado_por UUID REFERENCES usuario(id) ON DELETE SET NULL,
    atualizado_em TIMESTAMP WITH TIME ZONE DEFAULT NULL,
    encerrado_em TIMESTAMP WITH TIME ZONE DEFAULT NULL,

    CONSTRAINT uk_denuncia_reserva UNIQUE (reserva_id)
);

CREATE INDEX IF NOT EXISTS idx_denuncia_status ON denuncia(status);
CREATE INDEX IF NOT EXISTS idx_denuncia_tipo ON denuncia(tipo);
CREATE INDEX IF NOT EXISTS idx_denuncia_criado_em ON denuncia(criado_em);
CREATE INDEX IF NOT EXISTS idx_denuncia_vaga_id ON denuncia(vaga_id);
CREATE INDEX IF NOT EXISTS idx_denuncia_status_criado_em ON denuncia(status, criado_em DESC);

