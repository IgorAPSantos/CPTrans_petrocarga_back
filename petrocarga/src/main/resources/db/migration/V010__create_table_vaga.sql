CREATE TABLE vaga (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    autorizacao_id VARCHAR(255),
    localizacao_descricao TEXT,
    horario_inicio TIME,
    horario_fim TIME,
    max_veiculos INTEGER,
    status VARCHAR(50),
    endereco_id UUID NOT NULL,
    CONSTRAINT fk_vaga_endereco FOREIGN KEY (endereco_id) REFERENCES endereco_vaga(id)
);